import { Component, EventEmitter, inject, Input, OnInit, Output, signal, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { MediaQueryService } from '../../services/mq.service';
import { Router } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { AddMenu } from '../add-menu/add-menu';

@Component({
	selector: 'app-nav',
	standalone: true,
	templateUrl: './app-nav.html',
	styleUrls: ['./app-nav.css'],
	imports: [ReactiveFormsModule, CommonModule, FormsModule, ButtonModule, AddMenu]
})
export class AppNav {
	@Input() showCustomizeButton = true;
	@Input() showBackButton = false;
	@Input() showAddButton = false;
	@Input() showDeleteButton = false;
	@Input() deleteButtonVisible = false;
	@Input() showEditButton = false;
	@Input() editButtonVisible = false;
	@Output() backClick = new EventEmitter<void>();
	@Output() deleteClick = new EventEmitter<void>();
	@Output() editClick = new EventEmitter<void>();

	@ViewChild(AddMenu) addMenu!: AddMenu;

	#mediaService = inject(MediaQueryService);
	
	#router = inject(Router);

	visible = false;
	closed = true;
	isMobile = toSignal(this.#mediaService.mediaQuery('max', 'lg'));

	getSidebarTransition() {
		if (this.closed) {
			return "";
		} else {
			return "transform 0.3s";
		}
	}

	getSidebarTranslation(): string {
		let translation;

		if (this.isMobile()) {
			translation = this.visible ? 'translateX(0)' : 'translateX(-100%)';
		} else {
			translation = this.visible ? 'translateX(0)' : 'translateX(-768px)';
		}

		return translation;
	}

	open() {
		this.visible = true;
		this.closed = false;
	}

	close() {
		this.visible = false;

		setTimeout(() => this.closed = true, 300); // for the animation to play only when closing
		this.addMenu.resetForm();
	}

	customize() { this.#router.navigateByUrl('/edit'); }

	back(event: Event) {
		this.visible = false;
		this.closed = false;
		this.backClick.emit();
	}

	delete() {
		this.deleteClick.emit();
	}

	edit() {
		this.editClick.emit();
	}
}
