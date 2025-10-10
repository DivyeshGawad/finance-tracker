import { Routes } from '@angular/router';

export const routes: Routes = [

    {
        path:'',
        redirectTo:"dashboard",
        pathMatch:"full"
    },
    {
        path: "dashboard",
        loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent)
    },
    {
        path: "transactions",
        loadComponent: () => import('./features/transactions/transactions.component').then(m => m.TransactionsComponent)
    },
    {
        path: "budgets",
        loadComponent: () => import('./features/budgets/budgets.component').then(m => m.BudgetsComponent)
    },
    {
        path: "reports",
        loadComponent: () => import('./features/reports/reports.component').then(m => m.ReportsComponent)
    },
    {
        path: "**",
        loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent)
    }
];
