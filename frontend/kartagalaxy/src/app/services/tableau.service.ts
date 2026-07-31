import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';

import { TableauCard } from '../models/tableau-card';
import { TableauBit } from '../models/tableau-bit';
import { API_BASE_URL } from './api-config';

@Injectable({
  providedIn: 'root',
})
export class TableauService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/tableau`;
  private readonly cardsCache$ = new BehaviorSubject<Map<string, TableauCard>>(new Map());
  private readonly bitsCache$ = new BehaviorSubject<Map<string, TableauBit>>(new Map());

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

  deleteBit(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/bits/${id}`).pipe(
      tap(() => this.removebitFromCache(id)),
    );
  }

  getbits(): Observable<TableauBit[]> {
    return this.http.get<TableauBit[]>(`${this.baseUrl}/bits`).pipe(
      tap((items) => this.replacebitsCache(items)),
    );
  }

  getBitFromCache(id: string): Observable<TableauBit | undefined> {
    return this.bitsCache$.pipe(map((cache) => cache.get(id)));
  }

  getBitsFromCache(): Observable<TableauBit[]> {
    return this.bitsCache$.pipe(map((cache) => Array.from(cache.values())));
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

  private replacebitsCache(items: TableauBit[]): void {
    const nextCache = new Map<string, TableauBit>();

    items.forEach((item) => {
      if (item.id) {
        nextCache.set(item.id, item);
      }
    });

    this.bitsCache$.next(nextCache);
  }

  private removebitFromCache(id: string): void {
    const nextCache = new Map(this.bitsCache$.value);
    nextCache.delete(id);
    this.bitsCache$.next(nextCache);
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
