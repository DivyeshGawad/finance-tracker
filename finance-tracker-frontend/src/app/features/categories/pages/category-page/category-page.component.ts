import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { TableModule } from 'primeng/table';
import { ActionButtonsComponent } from '../../../../shared/components/action-buttons/action-buttons.component';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog.component';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';
import { Category } from '../../models/category.model';
import { CategoryFormComponent } from '../category-form/category-form.component';

@Component({
  selector: 'app-category-page',
  standalone: true,
  imports: [ CommonModule,
    FormsModule,
    TableModule,
    ButtonModule,
    DialogModule,
    PageHeaderComponent,
    ActionButtonsComponent,
    ConfirmDialogComponent,
    CategoryFormComponent],
  templateUrl: './category-page.component.html',
  styleUrl: './category-page.component.scss'
})
export class CategoryPageComponent {

  globalFilter = '';
  categories: Category[] = [];
  isDialogVisible = false;
  isEditMode = false;
  selectedCategory: Category | null = null;

  isConfirmVisible = false;
  categoryToDelete: Category | null = null;

  constructor() {
    this.categories = [
      { categoryId: 'SYS-INCOME-1', name: 'Salary', type: 'INCOME', isDefault: true },
      { categoryId: 'SYS-EXPENSE-1', name: 'Travel', type: 'EXPENSE', isDefault: true },
      { categoryId: 'SYS-EXPENSE-2', name: 'Food', type: 'EXPENSE', isDefault: true },
      { categoryId: 'SYS-EXPENSE-3', name: 'Entertainment', type: 'EXPENSE', isDefault: true },
      { categoryId: 'CAT-17595', name: 'Luxury', type: 'EXPENSE', isDefault: false },
      { categoryId: 'CAT-17596', name: 'Saving', type: 'INCOME', isDefault: false }
    ];
  }

  addCategory() {
    this.isDialogVisible = true;
    this.isEditMode = false;
    this.selectedCategory = null;
  }

  editCategory(category: Category) {
    this.selectedCategory = { ...category };
    this.isEditMode = true;
    this.isDialogVisible = true;
  }

  handleCategorySubmit(formData: Category) {
    if (this.isEditMode) {
      const index = this.categories.findIndex(c => c.categoryId === formData.categoryId);
      if (index !== -1) this.categories[index] = { ...formData };
    } else {
      const newCategory = {
        ...formData,
        categoryId: 'CAT-' + (this.categories.length + 1)
      };
      this.categories.push(newCategory);
    }
    this.isDialogVisible = false;
  }

  deleteCategory(category: Category) {
    this.categoryToDelete = category;
    this.isConfirmVisible = true;
  }

  onDeleteConfirmed() {
    if (this.categoryToDelete) {
      this.categories = this.categories.filter(c => c.categoryId !== this.categoryToDelete!.categoryId);
      this.categoryToDelete = null;
    }
    this.isConfirmVisible = false;
  }

  onDeleteCancelled() {
    this.categoryToDelete = null;
    this.isConfirmVisible = false;
  }
}