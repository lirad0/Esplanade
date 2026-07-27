import { Component, EventEmitter, inject, Input, OnInit, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { MediaQueryService } from '../../services/mq.service';
import { TableauService } from '../../services/tableau.service';
import { Router } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { TableauCard } from '../../models/tableau-card';

@Component({
	selector: 'app-nav',
	standalone: true,
	templateUrl: './app-nav.html',
	styleUrls: ['./app-nav.css'],
	imports: [ReactiveFormsModule, CommonModule, FormsModule, ButtonModule]
})
export class AppNav implements OnInit {
	@Input() showCustomizeButton = true;
	@Input() showBackButton = false;
	@Input() showAddButton = false;
	@Output() backClick = new EventEmitter<void>();

	#mediaService = inject(MediaQueryService);
	#tableauService = inject(TableauService);
	#router = inject(Router);

	visible = false;
	imageDataUrl = signal<string | null>(null);
	isMobile = toSignal(this.#mediaService.mediaQuery('max', 'md'));
	file: File | null = null;

	constructor(public fb: FormBuilder) { };

	form!: FormGroup;

	ngOnInit() {
		this.form = this.fb.group({
			name: [''],
			url: [null],
			file: [null]
		})
	}

	onFileChange(event: Event) {
		const input = event.target as HTMLInputElement;

		if (input.files && input.files[0]) {
			this.file = input.files[0];
			const reader = new FileReader();

			reader.onload = () => {
				this.imageDataUrl.set(reader.result as string);
			};

			reader.readAsDataURL(this.file);
		} else {
			this.imageDataUrl.set(null);
		}

	}

	getSidebarTransition(visible: boolean) {
		return visible ? 'transform 0.3s' : 'transform none';
	}

	getSidebarTranslation(visible: boolean): string {
		let translation;

		if (this.isMobile()) {
			translation = visible ? 'translateX(0)' : 'translateX(-100%)';
		} else {
			translation = visible ? 'translateX(0)' : 'translateX(-768px)';
		}

		return translation;
	}

	save() {
		// implement saving behavior as needed; currently closes the sidebar
		const formData = new FormData();

		Object.keys(
			this.form.controls
		)
			.forEach(
				formControlName => {
					const control = this.form.get(formControlName);
					
					let val;

					if (control?.value) {
						val = formControlName === "file" ? this.file : control?.value;
					} else {
						val = '';
					}
					
					formData.append(
						formControlName,
						val
					)
				}
			)

		this.#tableauService.saveCard(
			formData,
			''
		).subscribe(
			(v) => console.info(v)
		)
	}

	open() {
		this.visible = true;
	}

	close() {
		this.visible = false;
	}

	customize() { this.#router.navigateByUrl('/edit'); }

	back(event: Event) {
		this.visible = false;
		this.backClick.emit();
	}
}
