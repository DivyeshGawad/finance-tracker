import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-action-buttons',
  standalone: true,
  imports: [ButtonModule],
  templateUrl: './action-buttons.component.html',
  styleUrl: './action-buttons.component.scss'
})
export class ActionButtonsComponent {

  @Input() showEdit: boolean = true;
  @Input() showDelete: boolean = true;

  @Output() edit = new EventEmitter<any>();
  @Output() delete = new EventEmitter<any>();

  onEdit(){
    this.edit.emit();
  }

  onDelete(){
    this.delete.emit();
  }
}
