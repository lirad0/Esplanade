import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';

import { TableauCard } from '../models/tableau-card';
import { UrlOnlyItem } from '../models/url-only-item';
import { API_BASE_URL } from './api-config';

@Injectable({
  providedIn: 'root',
})
export class TableauService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/tableau`;
  private readonly cardsCache$ = new BehaviorSubject<Map<string, TableauCard>>(new Map());
  private readonly urlOnlyItemsCache$ = new BehaviorSubject<Map<string, UrlOnlyItem>>(new Map());

  getCards(): Observable<TableauCard[]> {
    return this.http.get<TableauCard[]>(`${this.baseUrl}/cards`).pipe(
      map((cards) => cards.map((card) => this.normalizeCard(card))),
      tap((cards) => this.replaceCardsCache(cards)),
    );
  }

  getCardFromCache(id: string): Observable<TableauCard | undefined> {
    return this.cardsCache$.pipe(map((cache) => cache.get(id)));
  }

  getCardsFromCache(): Observable<TableauCard[]> {
    return this.cardsCache$.pipe(map((cache) => Array.from(cache.values())));
  }

  saveCard(formData: FormData): Observable<TableauCard> {
    const id = formData.get("id");

    const request$ = id
      ? this.http.put<TableauCard>(`${this.baseUrl}/cards/${id}`, formData)
      : this.http.post<TableauCard>(`${this.baseUrl}/cards`, formData);

    return request$.pipe(
      map((card) => this.normalizeCard(card)),
      tap((card) => this.upsertCardInCache(card)),
    );
  }

  deleteCard(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/cards/${id}`).pipe(
      tap(() => this.removeCardFromCache(id)),
    );
  }

  deleteUrlOnlyItem(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/url-only-items/${id}`).pipe(
      tap(() => this.removeUrlOnlyItemFromCache(id)),
    );
  }

  getUrlOnlyItems(): Observable<UrlOnlyItem[]> {
    return this.http.get<UrlOnlyItem[]>(`${this.baseUrl}/url-only-items`).pipe(
      tap((items) => this.replaceUrlOnlyItemsCache(items)),
    );
  }

  getUrlOnlyItemFromCache(id: string): Observable<UrlOnlyItem | undefined> {
    return this.urlOnlyItemsCache$.pipe(map((cache) => cache.get(id)));
  }

  getUrlOnlyItemsFromCache(): Observable<UrlOnlyItem[]> {
    return this.urlOnlyItemsCache$.pipe(map((cache) => Array.from(cache.values())));
  }

  private normalizeCard(card: TableauCard): TableauCard {
    return { ...card, imageUrl: card.imageUrl ? this.resolveImageUrl(card.imageUrl) : null };
  }

  private replaceCardsCache(cards: TableauCard[]): void {
    const nextCache = new Map<string, TableauCard>();

    cards.forEach((card) => {
      if (card.id) {
        nextCache.set(card.id, this.normalizeCard(card));
      }
    });

    this.cardsCache$.next(nextCache);
  }

  private upsertCardInCache(card: TableauCard): void {
    const nextCache = new Map(this.cardsCache$.value);

    if (card.id) {
      nextCache.set(card.id, this.normalizeCard(card));
      this.cardsCache$.next(nextCache);
    }
  }

  private removeCardFromCache(id: string): void {
    const nextCache = new Map(this.cardsCache$.value);
    nextCache.delete(id);
    this.cardsCache$.next(nextCache);
  }

  private replaceUrlOnlyItemsCache(items: UrlOnlyItem[]): void {
    const nextCache = new Map<string, UrlOnlyItem>();

    items.forEach((item) => {
      if (item.id) {
        nextCache.set(item.id, item);
      }
    });

    this.urlOnlyItemsCache$.next(nextCache);
  }

  private removeUrlOnlyItemFromCache(id: string): void {
    const nextCache = new Map(this.urlOnlyItemsCache$.value);
    nextCache.delete(id);
    this.urlOnlyItemsCache$.next(nextCache);
  }

  private resolveImageUrl(imageUrl: string | undefined): string {
    if (!imageUrl) {
      return '';
    }

    if (/^https?:\/\//i.test(imageUrl) || imageUrl.startsWith('data:') || imageUrl.startsWith('/')) {
      return imageUrl;
    }

    return `${API_BASE_URL}/images/${encodeURIComponent(imageUrl)}`;
  }
}
