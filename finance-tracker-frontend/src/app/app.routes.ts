import { Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';

export const routes: Routes = [

    {
        path:'',
        redirectTo:"dashboard",
        pathMatch:'full'
    },
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
                loadComponent: () => import('./features/budgets/pages/budget-page/budget-page.component').then(m => m.BudgetPageComponent)
            },
            {
                path: "reports",
                loadComponent: () => import('./features/reports/pages/reports-page.component').then(m => m.ReportsPageComponent)
            },
            {
                path: "form-test",
                loadComponent: () => import('./features/budgets/pages/budget-form/budget-form.component').then(m => m.BudgetFormComponent)
            },
            {
                path: "**",
                loadComponent: () => import('./features/not-found/not-found.component').then(m => m.NotFoundComponent)
            }
        ]
    },
];
