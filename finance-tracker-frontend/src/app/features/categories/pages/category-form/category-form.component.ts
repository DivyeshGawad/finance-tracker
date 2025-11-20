import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Category } from '../../models/category.model';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { SelectModule } from 'primeng/select';

@Component({
  selector: 'app-category-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ButtonModule, SelectModule],
  templateUrl: './category-form.component.html',
  styleUrl: './category-form.component.scss'
})

export class CategoryFormComponent implements OnInit {

  @Input() category: Category | null = null;
  @Input() isEditMode = false;
  @Output() formSubmit = new EventEmitter<Category>();
  @Output() formCancel = new EventEmitter<void>();

  categoryForm!: FormGroup;

  typeOptions = [
    { label: 'Income', value: 'INCOME' },
    { label: 'Expense', value: 'EXPENSE' }
  ];

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.categoryForm = this.fb.group({
      categoryId: [this.category?.categoryId ?? ''],
      name: [this.category?.name ?? '', Validators.required],
      type: [this.category?.type ?? '', Validators.required],
      isDefault: [this.category?.isDefault ?? false]
    });

    // ❌ If default category → disable editing
    if (this.category?.isDefault) {
      this.categoryForm.disable();
    }
  }

  submitForm() {
    if (this.categoryForm.invalid) {
      this.categoryForm.markAllAsTouched();
      return;
    }
    this.formSubmit.emit(this.categoryForm.value);
  }

  cancel() {
    this.formCancel.emit();
  }
}
