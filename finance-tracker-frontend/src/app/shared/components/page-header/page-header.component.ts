import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from "../../../../../node_modules/@angular/common";
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-page-header',
  standalone: true,
  imports: [CommonModule,FormsModule],
  templateUrl: './page-header.component.html',
  styleUrl: './page-header.component.scss'
})
export class PageHeaderComponent {

  // Inputs let parent components send data IN
  @Input() title: string = '';
  @Input() emoji?: string | null = '';
  @Input() placeholder: string = 'Search..';
  @Input() showAddButtonText: string = 'Add';
  @Input() showSearch: boolean = true;
  @Input() showAddButton: boolean = true;
  @Input() icons:string='';
  @Input() tagLine:string='';

  // Outputs send events OUT to parent components
  @Output() searchChange = new EventEmitter<string>();
  @Output() addClick = new EventEmitter<void>();

  searchValue:string = '';

  onSearchChange(){
    this.searchChange.emit(this.searchValue);
  }

  onAddClick(){
    this.addClick.emit();
  }
}
