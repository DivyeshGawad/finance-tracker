import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CardModule } from 'primeng/card';
import { ChartModule } from 'primeng/chart';
import { ButtonModule } from 'primeng/button';
import { SelectModule } from 'primeng/select';
import { TableModule } from 'primeng/table';
import { InputTextModule } from 'primeng/inputtext';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-report-page',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    CardModule,
    ChartModule,
    ButtonModule,
    SelectModule,
    TableModule,
    InputTextModule,
    PageHeaderComponent
  ],
  templateUrl: './reports-page.component.html',
  styleUrl: './reports-page.component.scss'
})
export class ReportsPageComponent implements OnInit {

  // Filters
  startDate: string | null = null; // yyyy-MM-dd (input date)
  endDate: string | null = null;
  selectedCategories: string[] = [];
  selectedType: 'ALL' | 'INCOME' | 'EXPENSE' = 'ALL';
  searchText: string = '';

  // Dummy categories (for filter)
  categories = [
    { label: 'Food', value: 'Food' },
    { label: 'Transport', value: 'Transport' },
    { label: 'Shopping', value: 'Shopping' },
    { label: 'Salary', value: 'Salary' },
    { label: 'Entertainment', value: 'Entertainment' }
  ];

  // Summary cards data (computed from transactions)
  totalIncome = 0;
  totalExpense = 0;
  netBalance = 0;
  monthlySavings = 0;

  // Chart data
  pieData: any;
  pieOptions: any;

  lineData: any;
  lineOptions: any;

  // Transactions table (dummy)
  transactions: any[] = [];

  // For export
  datePipe = new DatePipe('en-US');

  ngOnInit(): void {
    this.initDummyTransactions();
    this.computeSummaries();
    this.preparePieChart();
    this.prepareLineChart();
  }

  initDummyTransactions() {
    // transactionDate in yyyy-MM-dd for easier input/date handling
    this.transactions = [
      { id: 'T1', date: '2025-11-01', category: 'Salary', type: 'INCOME', description: 'Monthly salary', amount: 50000 },
      { id: 'T2', date: '2025-11-02', category: 'Food', type: 'EXPENSE', description: 'Groceries', amount: 2400 },
      { id: 'T3', date: '2025-11-05', category: 'Transport', type: 'EXPENSE', description: 'Uber rides', amount: 800 },
      { id: 'T4', date: '2025-10-25', category: 'Shopping', type: 'EXPENSE', description: 'Clothes', amount: 4500 },
      { id: 'T5', date: '2025-09-15', category: 'Salary', type: 'INCOME', description: 'Bonus', amount: 10000 },
      { id: 'T6', date: '2025-11-08', category: 'Entertainment', type: 'EXPENSE', description: 'Movies', amount: 600 },
      { id: 'T7', date: '2025-10-10', category: 'Food', type: 'EXPENSE', description: 'Dinner', amount: 1200 },
      { id: 'T8', date: '2025-09-25', category: 'Salary', type: 'INCOME', description: 'Salary', amount: 50000 }
    ];
  }

  computeSummaries() {
    const filtered = this.applyFiltersToArray(this.transactions);
    this.totalIncome = filtered.filter(t => t.type === 'INCOME').reduce((s, t) => s + t.amount, 0);
    this.totalExpense = filtered.filter(t => t.type === 'EXPENSE').reduce((s, t) => s + t.amount, 0);
    this.netBalance = this.totalIncome - this.totalExpense;
    // monthlySavings: simple estimate = average monthly income - expenses (dummy)
    this.monthlySavings = Math.max(0, (this.totalIncome - this.totalExpense) / 1); // single-month view
  }

  applyFiltersToArray(arr: any[]) {
    return arr.filter(t => {
      // date filter
      if (this.startDate) {
        if (t.date < this.startDate) return false;
      }
      if (this.endDate) {
        if (t.date > this.endDate) return false;
      }
      // category filter
      if (this.selectedCategories && this.selectedCategories.length) {
        if (!this.selectedCategories.includes(t.category)) return false;
      }
      // type filter
      if (this.selectedType !== 'ALL' && t.type !== this.selectedType) return false;
      // search text
      if (this.searchText) {
        const s = this.searchText.toLowerCase();
        if (!(t.description.toLowerCase().includes(s) || t.category.toLowerCase().includes(s))) return false;
      }
      return true;
    });
  }

  preparePieChart() {
    // aggregate expense by category
    const filtered = this.applyFiltersToArray(this.transactions).filter(t => t.type === 'EXPENSE');
    const groups: Record<string, number> = {};
    filtered.forEach(t => groups[t.category] = (groups[t.category] || 0) + t.amount);
    const labels = Object.keys(groups);
    const data = labels.map(l => groups[l]);

    this.pieData = {
      labels,
      datasets: [{ data, backgroundColor: ['#10B981', '#3B82F6', '#F59E0B', '#EF4444', '#8B5CF6'] }]
    };
    this.pieOptions = {
      plugins: { legend: { position: 'bottom' } },
      responsive: true,
      maintainAspectRatio: false
    };
  }

  prepareLineChart() {
    // Create monthly series for last 6 months (dummy)
    const months = ['Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov'];
    // aggregate by month
    const incomeSeries = [0, 0, 0, 0, 0, 0];
    const expenseSeries = [0, 0, 0, 0, 0, 0];

    // map month string to index (naive using dates in dataset)
    this.transactions.forEach(t => {
      const d = new Date(t.date);
      const idx = months.indexOf(d.toLocaleString('en-US', { month: 'short' }));
      if (idx >= 0) {
        if (t.type === 'INCOME') incomeSeries[idx] += t.amount;
        else expenseSeries[idx] += t.amount;
      }
    });

    this.lineData = {
      labels: months,
      datasets: [
        { label: 'Income', data: incomeSeries, fill: false, tension: 0.4, borderWidth: 2, borderColor: '#3B82F6' },
        { label: 'Expense', data: expenseSeries, fill: false, tension: 0.4, borderWidth: 2, borderColor: '#EF4444' }
      ]
    };

    this.lineOptions = {
      plugins: { legend: { position: 'bottom' } },
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        y: { beginAtZero: true }
      }
    };
  }

  applyFilters() {
    this.computeSummaries();
    this.preparePieChart();
    this.prepareLineChart();
  }

  resetFilters() {
    this.startDate = null;
    this.endDate = null;
    this.selectedCategories = [];
    this.selectedType = 'ALL';
    this.searchText = '';
    this.applyFilters();
  }

  exportCSV() {
    // simple CSV export of filtered transactions
    const filtered = this.applyFiltersToArray(this.transactions);
    const headers = ['Date', 'Category', 'Type', 'Description', 'Amount'];
    const rows = filtered.map(t => [t.date, t.category, t.type, `"${t.description}"`, t.amount]);
    const csv = [headers.join(','), ...rows.map(r => r.join(','))].join('\n');
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `transactions_${new Date().toISOString().slice(0,10)}.csv`;
    a.click();
    URL.revokeObjectURL(url);
  }

}
