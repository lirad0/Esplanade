import { Component, EventEmitter, inject, Input, Output, signal } from "@angular/core";
import { GenericCard } from "../generic-card/generic-card";
import { WeatherCard } from "../weather-card/weather-card";
import { TableauService } from "../../services/tableau.service";
import { TableauCard } from "../../models/tableau-card";
import { TableauBit } from "../../models/tableau-bit";
import { NotificationService } from "../../services/notification.service";
import { take } from "rxjs/internal/operators/take";

@Component({
  imports: [GenericCard, WeatherCard],
  selector: "tableau",
  templateUrl: "./tableau.html"
})
export class Tableau {
  @Input() editMode = false;
  @Output() selectionChange = new EventEmitter<{ count: number; selectedId: string | null }>();

  private readonly tableauService = inject(TableauService);
  private readonly notificationService = inject(NotificationService);

  protected readonly cards = signal<TableauCard[]>([]);
  protected readonly bits = signal<TableauBit[]>([]);
  protected readonly selectedItemIds = signal<string[]>([]);

  constructor() {
    this.refresh();

    this.notificationService.on("tableau::refresh").subscribe(() => this.refresh());
  }

  refresh() {
    this.tableauService.getCards().subscribe((cards) => this.cards.set(cards));
    this.tableauService.getBits().subscribe((bits) => this.bits.set(bits));
  }

  protected removeCard(id: string): void {
    this.cards.set(this.cards().filter((card) => card.id !== id));
  }

  protected removeBit(id: string): void {
    this.bits.set(this.bits().filter((item) => item.id !== id));
  }

  protected isSelected(id: string): boolean {
    return this.selectedItemIds().includes(id);
  }

  protected toggleSelection(id: string): void {
    if (!this.editMode) {
      return;
    }

    const currentSelection = this.selectedItemIds();
    const nextSelection = currentSelection.includes(id)
      ? currentSelection.filter((selectedId) => selectedId !== id)
      : [...currentSelection, id];

    this.selectedItemIds.set(nextSelection);
    this.selectionChange.emit({
      count: nextSelection.length,
      selectedId: nextSelection.length === 1 ? nextSelection[0] : null,
    });
  }

  public deleteSelectedItems(): void {
    const selectedIds = this.selectedItemIds();

    if (!selectedIds.length) {
      return;
    }

    selectedIds.forEach((id) => {
      const isCard = this.cards().some((card) => card.id === id);

      if (isCard) {
        this.tableauService.deleteCard(id).subscribe({
          next: () => this.removeCard(id),
          error: (error) => console.error('Failed to delete card', error),
        });
        return;
      }

      this.tableauService.deleteBit(id).subscribe({
        next: () => this.removeBit(id),
        error: (error) => console.error('Failed to delete weather item', error),
      });
    });

    this.selectedItemIds.set([]);
    this.selectionChange.emit({ count: 0, selectedId: null });
  }

  public editSelectedItem(): void {
    const selectedId = this.selectedItemIds()[0];

    if (!selectedId) {
      return;
    }

    this.tableauService.getBitFromCache(selectedId).pipe(take(1)).subscribe((bit) => {
      if (bit) {
        this.notificationService.sendNotification('tableau::editBit', { id: selectedId, url: bit.url });
      }
    }); 

    this.tableauService.getCardFromCache(selectedId).pipe(take(1)).subscribe((card) => {
      if (card) {
        this.notificationService.sendNotification('tableau::editCard', { id: selectedId, name: card.name, url: card.url, imageUrl: card.imageUrl });
      }
    }); 
  }
}
