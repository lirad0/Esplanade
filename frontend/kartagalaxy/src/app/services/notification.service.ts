import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';

interface NotificationPayload<T = any> {
  key: string;
  data: T;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private notificationSource = new Subject<NotificationPayload>();

  /**
   * Send a notification with a key and any type of data
   * (string, number, array, object, etc.)
   */
  sendNotification<T = any>(key: string, data?: T): void {
    this.notificationSource.next({ key, data });
  }

  /**
   * Subscribe to notifications matching a specific key.
   * Only emits when sendNotification was called with that exact key.
   */
  on<T = any>(key: string): Observable<T> {
    return this.notificationSource.asObservable().pipe(
      filter(notification => notification.key === key),
      map(notification => notification.data as T)
    );
  }
}