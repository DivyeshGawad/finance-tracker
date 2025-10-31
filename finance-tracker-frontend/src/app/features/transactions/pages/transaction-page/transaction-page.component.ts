import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { FormsModule } from '@angular/forms';
import { DialogModule } from 'primeng/dialog';
import { TransactionFormComponent } from '../transaction-form/transaction-form.component';
import { ConfirmationService, MessageService } from 'primeng/api';
import {ConfirmDialogModule} from 'primeng/confirmdialog';
import { Transaction } from '../../models/transaction.model';

@Component({
  selector: 'app-transaction-page',
  standalone: true,
  imports: [CommonModule, TableModule, ButtonModule, FormsModule, DialogModule, TransactionFormComponent,ConfirmDialogModule],
  providers: [ConfirmationService, MessageService],
  templateUrl: './transaction-page.component.html',
  styleUrl: './transaction-page.component.scss'
})
export class TransactionPageComponent {
  globalFilter: string = '';
  transactions: Transaction[] = [];
  isDialogVisible = false;
  isEditMode = false;
  selectedTransaction: any = null;

  constructor(private confirmService: ConfirmationService) {
    // Mock Data
    this.transactions = [
      { transactionId: 'T1', transactionDate: '2025-10-12', categoryName: 'Salary', categoryType: 'Income', description: 'Monthly Salary', amount: 50000 },
      { transactionId: 'T2', transactionDate: '2025-10-13', categoryName: 'Groceries', categoryType: 'Expense', description: 'Monthly groceries', amount: 2500 },
      { transactionId: 'T3', transactionDate: '2025-10-13', categoryName: 'Rent', categoryType: 'Expense', description: 'House rent', amount: 12000 }
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
    this.confirmService.confirm({
      message: `Are you sure you want to delete "${transaction.categoryName}"?`,
      header: 'Confirm Delete',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.transactions = this.transactions.filter(t => t.transactionId !== transaction.transactionId);
      }
    });
  }
}
