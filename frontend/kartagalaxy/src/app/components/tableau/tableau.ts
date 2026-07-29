import { Component, EventEmitter, inject, Input, Output, signal } from "@angular/core";
import { GenericCard } from "../generic-card/generic-card";
import { WeatherCard } from "../weather-card/weather-card";
import { TableauService } from "../../services/tableau.service";
import { TableauCard } from "../../models/tableau-card";
import { UrlOnlyItem } from "../../models/url-only-item";

@Component({
  imports: [GenericCard, WeatherCard],
  selector: "tableau",
  templateUrl: "./tableau.html"
})
export class Tableau {
  @Input() editMode = false;
  @Output() selectionChange = new EventEmitter<{ count: number; selectedId: string | null }>();

  private readonly tableauService = inject(TableauService);

  protected readonly cards = signal<TableauCard[]>([]);
  protected readonly urlOnlyItems = signal<UrlOnlyItem[]>([]);
  protected readonly selectedItemIds = signal<string[]>([]);

  constructor() {
    this.tableauService.getCards().subscribe((cards) => this.cards.set(cards));
    this.tableauService
      .getUrlOnlyItems()
      .subscribe((items) => this.urlOnlyItems.set(items));
  }

  protected removeCard(id: string): void {
    this.cards.set(this.cards().filter((card) => card.id !== id));
  }

  protected removeUrlOnlyItem(id: string): void {
    this.urlOnlyItems.set(this.urlOnlyItems().filter((item) => item.id !== id));
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

      this.tableauService.deleteUrlOnlyItem(id).subscribe({
        next: () => this.removeUrlOnlyItem(id),
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

    console.info('Edit selected item', selectedId);
  }
}
