import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges, OnChanges, ViewEncapsulation } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { SelectModule } from 'primeng/select';
import { InputTextModule } from 'primeng/inputtext';
import { Transaction } from '../../models/transaction.model';

@Component({
  selector: 'app-transaction-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    ButtonModule,
    SelectModule,
    InputTextModule
  ],
  templateUrl: './transaction-form.component.html',
  styleUrl: './transaction-form.component.scss',
  encapsulation: ViewEncapsulation.None
})
export class TransactionFormComponent implements OnInit, OnChanges {
  @Input() transaction: Transaction = {};
  @Input() isEditMode: boolean = false;
  @Output() formSubmit = new EventEmitter<Transaction>();
  @Output() formCancel = new EventEmitter<Transaction>();

  transactionForm!: FormGroup;

  typeOptions = [
    { label: 'Income', value: 'Income' },
    { label: 'Expense', value: 'Expense' }
  ];

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.transactionForm = this.fb.group({
      transactionDate: ['', Validators.required],
      categoryName: ['', Validators.required],
      categoryType: ['', Validators.required],
      description: ['', [Validators.required, Validators.maxLength(100)]],
      amount: ['', [Validators.required, Validators.min(1)]]
    });
  }

  // 🔥 This is the important part
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['transaction'] && this.transactionForm) {
      this.transactionForm.patchValue({
        transactionDate: this.transaction?.transactionDate || '',
        categoryName: this.transaction?.categoryName || '',
        categoryType: this.transaction?.categoryType || '',
        description: this.transaction?.description || '',
        amount: this.transaction?.amount || ''
      });
    }
  }

  submitForm() {
    if (this.transactionForm.valid) {
      const formData = { ...this.transaction, ...this.transactionForm.value };
      this.formSubmit.emit(formData);
    } else {
      this.transactionForm.markAllAsTouched();
    }
  }

  cancelForm() {
    this.formCancel.emit();
  }
}
