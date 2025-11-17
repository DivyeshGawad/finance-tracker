import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { SelectModule } from 'primeng/select';
import { Budget } from '../../models/budget.model';
import { InputNumberModule } from 'primeng/inputnumber';
import { InputTextModule } from 'primeng/inputtext';
import { DatePickerModule } from 'primeng/datepicker';

@Component({
  selector: 'app-budget-form',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, SelectModule, ButtonModule, InputNumberModule, InputTextModule, DatePickerModule],
  templateUrl: './budget-form.component.html',
  styleUrl: './budget-form.component.scss'
})
export class BudgetFormComponent {

  @Input() budget: Budget | null = null;
  @Input() isEditMode = false;
  @Output() formSubmit = new EventEmitter<any>();
  @Output() formCancel = new EventEmitter<void>();

  categoryOptions = [
    { label: 'Food', value: 'cat001' },
    { label: 'Entertainment', value: 'cat002' },
    { label: 'Transport', value: 'cat003' },
    { label: 'Shopping', value: 'cat004' },
    { label: 'Health', value: 'cat005' },
  ];

  budgetForm!: FormGroup;

  constructor(private fb: FormBuilder) { }

  ngOnInit(): void {
    this.budgetForm = this.fb.group({
      name: [this.budget?.name || '', Validators.required],
      categoryId: [this.budget?.categoryId || '', Validators.required],
      note: [this.budget?.note || '', Validators.required],
      budgetAmount: [this.budget?.budgetAmount || '', Validators.required],
      spendAmount: [this.budget?.spendAmount || '', Validators.required],
      startDate: [this.budget?.startDate || '', Validators.required],
      endDate: [this.budget?.endDate || '', Validators.required],
    });
  }


  // 🔥 This is the important part
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['budget'] && this.budgetForm) {
      this.budgetForm.patchValue({
        name: this.budget?.name || '',
        categoryId: this.budget?.categoryId || '',
        note: this.budget?.note || '',
        budgetAmount: this.budget?.budgetAmount || '',
        spendAmount: this.budget?.spendAmount || '',
        startDate: this.toInputDate(this.budget?.startDate),
      endDate: this.toInputDate(this.budget?.endDate)
      });
    }
  }

  submitForm() {
    if (this.budgetForm.valid) {
      const data = { ...this.budget, ...this.budgetForm.value };
      // 🗓️ Convert startDate and endDate to dd-MM-yyyy
      data.startDate = this.formatDate(data.startDate);
      data.endDate = this.formatDate(data.endDate);
      console.log('Budget Form submitted:', data);
      this.formSubmit.emit(data);
    } else {
      this.budgetForm.markAllAsTouched();
    }
  }

  cancelForm() {
    this.formCancel.emit();
  }

  // 🔥 Helper function to format date as dd-MM-yyyy
  formatDate(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${day}-${month}-${year}`;
  }
  toInputDate(dateStr: string | undefined): string {
  if (!dateStr) return '';

  const parts = dateStr.split('-'); // Format: dd-MM-yyyy
  const day = parts[0];
  const month = parts[1];
  const year = parts[2];

  return `${year}-${month}-${day}`; // Format: yyyy-MM-dd
}

}