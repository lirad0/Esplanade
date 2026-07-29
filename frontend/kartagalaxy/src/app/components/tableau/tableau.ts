import { Component, inject, Input, signal } from "@angular/core";
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

  private readonly tableauService = inject(TableauService);

  protected readonly cards = signal<TableauCard[]>([]);
  protected readonly urlOnlyItems = signal<UrlOnlyItem[]>([]);

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
}
