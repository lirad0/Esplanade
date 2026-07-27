import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { TableauService } from '../../services/tableau.service';

@Component({
  selector: 'weather-card',
  templateUrl: './weather-card.html'
})

export class WeatherCard {
    @Input('data-id') dataId = '';
    @Input() url = '';
    @Input() editMode = false;
    @Output() deleted = new EventEmitter<void>();

    private readonly tableauService = inject(TableauService);
    constructor(private sanitizer: DomSanitizer) {}

    get safeUrl(): SafeResourceUrl {
        return this.sanitizer.bypassSecurityTrustResourceUrl(this.url);
    }

    handleDelete(): void {
        if (!this.dataId) {
            return;
        }

        this.tableauService.deleteUrlOnlyItem(this.dataId).subscribe({
            next: () => this.deleted.emit(),
            error: (error) => console.error('Failed to delete weather item', error),
        });
    }
}