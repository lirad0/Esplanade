import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { CardModule } from 'primeng/card';
import { TableauService } from '../../services/tableau.service';

@Component({
  selector: 'generic-card',
  templateUrl: './generic-card.html',
  imports: [CardModule],
})

export class GenericCard {
  @Input('data-id') dataId = '';
  @Input() name!: string;
  @Input() img!: string;
  @Input() url!: string;
  @Input() editMode = false;
  @Output() deleted = new EventEmitter<void>();

  private readonly tableauService = inject(TableauService);

  handleDelete(): void {
    if (!this.dataId) {
      return;
    }

    this.tableauService.deleteCard(this.dataId).subscribe({
      next: () => this.deleted.emit(),
      error: (error) => console.error('Failed to delete card', error),
    });
  }
}
