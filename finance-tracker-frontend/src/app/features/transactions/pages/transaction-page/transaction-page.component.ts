import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { FormsModule } from '@angular/forms';
import { DialogModule } from 'primeng/dialog';
import { TransactionFormComponent } from '../transaction-form/transaction-form.component';
import { Transaction } from '../../models/transaction.model';
import { PageHeaderComponent } from "../../../../shared/components/page-header/page-header.component";
import { ActionButtonsComponent } from "../../../../shared/components/action-buttons/action-buttons.component";
import { ConfirmDialogComponent } from "../../../../shared/components/confirm-dialog/confirm-dialog.component";

@Component({
  selector: 'app-transaction-page',
  standalone: true,
  imports: [CommonModule, TableModule, ButtonModule, FormsModule, DialogModule, TransactionFormComponent, PageHeaderComponent, ActionButtonsComponent, ConfirmDialogComponent],
  templateUrl: './transaction-page.component.html',
  styleUrl: './transaction-page.component.scss'
})
export class TransactionPageComponent {
  globalFilter: string = '';
  transactions: Transaction[] = [];
  isDialogVisible = false;
  isEditMode = false;
  selectedTransaction: any = null;
  isConfirmVisible = false;
  transactionToDelete: any = null;

  constructor() {
    // Mock Data
    this.transactions = [
      { transactionId: 'T1', transactionDate: '12-10-2025', categoryName: 'Salary', categoryType: 'Income', description: 'Monthly Salary', amount: 50000 },
      { transactionId: 'T2', transactionDate: '13-10-2025', categoryName: 'Groceries', categoryType: 'Expense', description: 'Monthly groceries', amount: 2500 },
      { transactionId: 'T3', transactionDate: '13-10-2025', categoryName: 'Rent', categoryType: 'Expense', description: 'House rent', amount: 12000 }
    ];
  }

  addTransaction() {
    this.isDialogVisible = true;
    this.isEditMode = false;
    this.selectedTransaction = {};
  }

  editTransaction(transaction: any) {
    this.selectedTransaction = { ...transaction };
    this.isEditMode = true;
    this.isDialogVisible = true;
  }

  handleTransactionSubmit(formData: any) {
    if (this.isEditMode) {
      const index = this.transactions.findIndex(t => t.transactionId === formData.transactionId);
      if (index !== -1) this.transactions[index] = { ...formData };
    } else {
      const newTransaction = {
        ...formData,
        transactionId: 'T' + (this.transactions.length + 1)
      };
      this.transactions.push(newTransaction);
    }
    this.isDialogVisible = false;
  }

  deleteTransaction(transaction: any) {
    debugger;
    this.transactionToDelete = transaction;
    this.isConfirmVisible = true;

  }

  onDeleteConfirmed() {
    if (this.transactionToDelete) {
      this.transactions = this.transactions.filter(
        t => t.transactionId !== this.transactionToDelete.transactionId
      );
      this.transactionToDelete = null;
    }
    this.isConfirmVisible = false;
  }

  onDeleteCancelled() {
    this.transactionToDelete = null;
    this.isConfirmVisible = false;
  }

}
