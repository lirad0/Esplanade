import { CommonModule } from "@angular/common";
import { Component, ElementRef, EventEmitter, inject, OnInit, Output, signal, ViewChild } from "@angular/core";
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from "@angular/forms";
import { ButtonModule } from "primeng/button";
import { TableauService } from "../../services/tableau.service";
import { NotificationService } from "../../services/notification.service";
import { TableauCard } from "../../models/tableau-card";

@Component({
    selector: 'add-menu',
    standalone: true,
    templateUrl: './add-menu.html',
    imports: [ReactiveFormsModule, CommonModule, FormsModule, ButtonModule]
})
export class AddMenu implements OnInit {
	@ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

    #tableauService = inject(TableauService);
    #notificationService = inject(NotificationService);

    file: File | null = null;
    imageDataUrl = signal<string | null>(null);
    form!: FormGroup;

    @Output() openSlide = new EventEmitter<void>();
    @Output() closeSlide = new EventEmitter<void>();

	constructor(public fb: FormBuilder) { };

    ngOnInit(): void {
        this.form = this.fb.group({
            id: [''],
            name: [''],
            url: [null],
            file: [null]
        });

        this.#notificationService
            .on<TableauCard>('tableau::edit')
            .subscribe(data => {
                this.form.setValue({
                    id: data.id,
                    name: data.name,
                    url: data.url,
                    file: null
                });

                this.imageDataUrl.set(data.imageUrl);

                this.#notificationService.sendNotification("appnav::openSlide");

                this.openSlide.emit();
            });
    }

    save() {
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
            formData
        ).subscribe(
            (v) => console.info(v)
        )
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

    triggerCloseSlide() {
        this.closeSlide.emit();
    }

    triggerFileInput() {
        this.fileInput.nativeElement.click();
    }

    resetForm() {
        this.form.setValue({
			id: [''],
			name: [''],
			url: [null],
			file: [null]
		})

		this.imageDataUrl.set('');
    }
}