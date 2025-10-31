import { Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';

export const routes: Routes = [

    {
        path: '',
        component: MainLayoutComponent,
        children: [
            {
                path: "dashboard",
                loadComponent: () => import('./features/dashboard/pages/dashboard-page.component').then(m => m.DashboardPageComponent)
            },
            {
                path: "transactions",
                loadComponent: () => import('./features/transactions/pages/transaction-page/transaction-page.component').then(m => m.TransactionPageComponent)
            },
            {
                path: "budgets",
                loadComponent: () => import('./features/budgets/pages/budgets-page.component').then(m => m.BudgetsPageComponent)
            },
            {
                path: "reports",
                loadComponent: () => import('./features/reports/pages/reports-page.component').then(m => m.ReportsPageComponent)
            },
            {
                path: "**",
                loadComponent: () => import('./features/dashboard/pages/dashboard-page.component').then(m => m.DashboardPageComponent)
            }
        ]
    }
];
