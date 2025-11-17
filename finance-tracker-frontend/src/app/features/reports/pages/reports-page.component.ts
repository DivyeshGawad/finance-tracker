import { Component } from '@angular/core';
import { PageHeaderComponent } from "../../../shared/components/page-header/page-header.component";

@Component({
  selector: 'app-reports-page',
  standalone: true,
  imports: [PageHeaderComponent],
  templateUrl: './reports-page.component.html',
  styleUrl: './reports-page.component.scss'
})
export class ReportsPageComponent {

}
