import { Component } from '@angular/core';
import { CardModule } from 'primeng/card';
import {ChartModule} from 'primeng/chart';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CardModule, ChartModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent {
  chartData: any;
  chartOptions: any;

  constructor() {
    this.chartData = {
      labels: ['Food', 'Rent', 'Transport', 'Shopping', 'Others'],
      datasets: [
        {
          data: [3000, 8000, 2000, 1500, 500],
          backgroundColor: ['#10B981', '#3B82F6', '#F59E0B', '#EF4444', '#8B5CF6'],
          hoverBackgroundColor: ['#34D399', '#60A5FA', '#FBBF24', '#F87171', '#A78BFA']
        }
      ]
    };

    this.chartOptions = {
      plugins: {
        legend: {
          position: 'bottom',
          labels: {
            color: '#374151',
            font: { size: 13 }
          }
        }
      },
      responsive: true,
      maintainAspectRatio: false
    };
  }
}
