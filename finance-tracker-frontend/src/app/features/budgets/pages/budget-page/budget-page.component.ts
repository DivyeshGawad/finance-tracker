import { CommonModule, DatePipe } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { BudgetFormComponent } from '../budget-form/budget-form.component';
import { Budget } from '../../models/budget.model';
import { PageHeaderComponent } from "../../../../shared/components/page-header/page-header.component";
import { ActionButtonsComponent } from "../../../../shared/components/action-buttons/action-buttons.component";
import { ConfirmDialogComponent } from "../../../../shared/components/confirm-dialog/confirm-dialog.component";

@Component({
  selector: 'app-budget-page',
  standalone: true,
  imports: [CommonModule, FormsModule, TableModule, ButtonModule, DialogModule, BudgetFormComponent, PageHeaderComponent, ActionButtonsComponent, ConfirmDialogComponent],
  templateUrl: './budget-page.component.html',
  styleUrl: './budget-page.component.scss'
})
export class BudgetPageComponent {
  globalFilter = '';
  budgets: Budget[] = [];
  isDialogVisible = false;
  isEditMode = false;
  selectedBudget: Budget | null = null;
  isConfirmVisible = false;
  budgetToDelete: any = null;

  constructor() {
    this.budgets = [
      {
        budgetId: 'BUD-1759059189708343',
        userId: '1757693422108196',
        name: 'Monthly Budget',
        categoryId: 'SYS-EXPENSE-3',
        budgetAmount: 12000,
        spendAmount: 14536,
        note: 'Updated September Budget',
        startDate: '01-09-2025',
        endDate: '30-09-2025',
        createdAt: '28-09-2025 17:03:09',
        updatedAt: '07-10-2025 15:56:03',
        percentUsed: 82,
        status: 'EXCEEDED',
      },
      {
        budgetId: 'BUD-1759587554959903',
        userId: '1757693422108196',
        name: 'Yearly Budget For Luxury',
        categoryId: 'CAT-1759587226121449',
        budgetAmount: 25000,
        spendAmount: 2000,
        note: 'Annual Budget for luxury items',
        startDate: '01-01-2025',
        endDate: '31-12-2025',
        createdAt: '04-10-2025 19:49:14',
        updatedAt: '04-10-2025 19:50:33',
        percentUsed: 8,
        status: 'ON_TRACK',
      }
    ];
  }

  addBudget() {
    this.isDialogVisible = true;
    this.isEditMode = false;
    this.selectedBudget = null;
  }

  editBudget(budget: Budget) {
    this.selectedBudget = { ...budget };
    this.isEditMode = true;
    this.isDialogVisible = true;
  }

  handleBudgetSubmit(formData: Budget) {
    if (this.isEditMode) {
      const index = this.budgets.findIndex(b => b.budgetId === formData.budgetId);
      console.log("Edit form",formData);
      
      if (index !== -1) this.budgets[index] = { ...formData };
    } else {
      const newBudget = {
        ...formData,
        budgetId: 'B' + (this.budgets.length + 1)
      };
      console.log(newBudget);
      
      this.budgets.push(newBudget);
    }

    this.isDialogVisible = false;
  }

  getPercentColor(percent: number): string {
    if (percent <= 80) return '#22c55e'; // green
    if (percent <= 100) return '#facc15'; // yellow
    return '#ef4444'; // red
  }

  deleteBudget(budget: any) {
    this.budgetToDelete = budget;
    this.isConfirmVisible = true;

  }

  onDeleteConfirmed() {
    if (this.budgetToDelete) {
      console.log(this.budgetToDelete);

      this.budgets = this.budgets.filter(
        t => t.budgetId !== this.budgetToDelete.budgetId
      );
      this.budgetToDelete = null;
    }
    this.isConfirmVisible = false;
  }

  onDeleteCancelled() {
    this.budgetToDelete = null;
    this.isConfirmVisible = false;
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
}
