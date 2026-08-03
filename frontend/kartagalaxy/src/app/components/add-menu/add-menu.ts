import { CommonModule } from "@angular/common";
import { Component, ElementRef, EventEmitter, inject, Input, OnInit, Output, Signal, signal, ViewChild, WritableSignal } from "@angular/core";
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from "@angular/forms";
import { ButtonModule } from "primeng/button";
import { TableauService } from "../../services/tableau.service";
import { NotificationService } from "../../services/notification.service";
import { TableauCard } from "../../models/tableau-card";
import { MenuStage } from "../enums/menu-stage.enum";
import { TableauBit } from "../../models/tableau-bit";

@Component({
    selector: 'add-menu',
    standalone: true,
    templateUrl: './add-menu.html',
    imports: [ReactiveFormsModule, CommonModule, FormsModule, ButtonModule]
})
export class AddMenu implements OnInit {
	MenuStage = MenuStage;
    
    @Output() openSlide = new EventEmitter<void>();
    @Output() closeSlide = new EventEmitter<void>();

    #tableauService = inject(TableauService);
    #notificationService = inject(NotificationService);

    title: String = "Add";

    stage: WritableSignal<MenuStage | undefined> = signal(undefined);

    /* Link Form */
    @ViewChild('linkFormFileInput') linkFormFileInput!: ElementRef<HTMLInputElement>;
    linkFormFile: File | null = null;
    linkFormImageDataUrl = signal<string | null>(null);
    linkFormGroup!: FormGroup;

    /* Iframe Form */
    iframeFormGroup!: FormGroup;
    
	constructor(public fb: FormBuilder) { };

    ngOnInit(): void {  
        this.linkFormGroup = this.fb.group({
            id: [''],
            name: [''],
            url: [null],
            file: [null]
        });

        this.iframeFormGroup = this.fb.group({
            id: [''],
            url: [null],
        });

        this.#notificationService
            .on<TableauCard>('tableau::editCard')
            .subscribe(data => {
                this.linkFormGroup.setValue({
                    id: data.id,
                    name: data.name,
                    url: data.url,
                    file: null
                });

                this.linkFormImageDataUrl.set(data.imageUrl);

                this.#notificationService.sendNotification("appnav::openSlide");

                this.title = "Edit";

                this.stage.set(MenuStage.LINK);

                this.openSlide.emit();
            });

        this.#notificationService
            .on<TableauBit>('tableau::editBit')
            .subscribe(data => {
                this.iframeFormGroup.setValue({
                    id: data.id,
                    url: data.url,
                });

                this.#notificationService.sendNotification("appnav::openSlide");

                this.title = "Edit";

                this.stage.set(MenuStage.IFRAME);

                this.openSlide.emit();
            });
    }

    refreshTableau() {
        this.#notificationService.sendNotification("tableau::refresh");
    }

    saveLinkForm() {
        const formData = new FormData();

        Object.keys(
            this.linkFormGroup.controls
        )
            .forEach(
                formControlName => {
                    const control = this.linkFormGroup.get(formControlName);

                    let val;

                    if (control?.value) {
                        val = formControlName === "file" ? this.linkFormFile : control?.value;
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
            (v) => this.refreshTableau()
        )
    }

    saveIframeForm() {
        const formData = new FormData();

        Object.keys(
            this.iframeFormGroup.controls
        )
            .forEach(
                formControlName => {
                    const control = this.iframeFormGroup.get(formControlName);

                    let val;

                    if (control?.value) {
                        val = control?.value;
                    } else {
                        val = '';
                    }

                    formData.append(
                        formControlName,
                        val
                    )
                }
            )

        this.#tableauService.saveBit(
            formData
        ).subscribe(
            (v) => this.refreshTableau()
        )
    }

    onFileChange(event: Event) {
        const input = event.target as HTMLInputElement;

        if (input.files && input.files[0]) {
            this.linkFormFile = input.files[0];
            const reader = new FileReader();

            reader.onload = () => {
                this.linkFormImageDataUrl.set(reader.result as string);
            };

            reader.readAsDataURL(this.linkFormFile);
        } else {
            this.linkFormImageDataUrl.set(null);
        }

    }

    triggerCloseSlide() {
        this.closeSlide.emit();
    }

    triggerFileInput() {
        this.linkFormFileInput.nativeElement.click();
    }

    resetForm() {
        this.linkFormGroup.setValue({
			id: [''],
			name: [''],
			url: [null],
			file: [null]
		})

		this.linkFormImageDataUrl.set('');

        this.title = "Add";

        this.stage.set(undefined);
    }
}