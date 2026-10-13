import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Api } from '../../services/api';
import { CreateInquiryRequest } from '../../services/models';
import { createInquiry } from '../../services/functions';

@Component({
  selector: 'app-contact-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './contact-page.component.html',
})
export class ContactPageComponent {
  inquiryForm: FormGroup;
  isSubmitting = false;
  isDonateOpen = false;
  showForm = false;

  readonly partnerOptions = [
    {
      title: 'NGO & Community Partners',
      description: 'Collaborate on projects and share expertise.',
      color: 'primary',
      type: 'PARTNERSHIP',
    },
    {
      title: 'Corporate Sponsorship',
      description: 'Support our programs and initiatives.',
      color: 'orange',
      type: 'PARTNERSHIP',
    },
    {
      title: 'Skill Development',
      description: 'Offer training, mentoring, or technical support.',
      color: 'green',
      type: 'VOLUNTEER',
    },
    {
      title: 'Institutional Support',
      description: 'Work together on grants and research.',
      color: 'primary',
      type: 'PARTNERSHIP',
    },
  ];

  /** Replace with real bank details when ready */
  readonly bankAccounts = [
    {
      bank: 'Commercial Bank of Ethiopia',
      accountName: 'Emmaus Companions',
      accountNumber: 'XXXXXXXXXXXX',
      branch: 'Bahir Dar',
    },
    // add more if needed
  ];

  constructor(
    private fb: FormBuilder,
    private api: Api,
    private toastr: ToastrService,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {
    this.inquiryForm = this.fb.group({
      senderName: ['', Validators.required],
      senderEmail: ['', [Validators.required, Validators.email]],
      senderPhone: [''],
      subject: ['', Validators.required],
      type: ['GENERAL', Validators.required],
      message: ['', Validators.required],
    });

    // Deep-link: /contact?type=PARTNERSHIP or ?type=VOLUNTEER or ?donate=1
    this.route.queryParamMap.subscribe((params) => {
      const type = params.get('type');
      if (type) {
        this.openForm(type);
      }
      if (params.get('donate') === '1') {
        this.isDonateOpen = true;
      }
    });
  }

  openForm(type: string = 'GENERAL', subjectHint?: string) {
    this.inquiryForm.patchValue({
      type,
      subject:
        subjectHint ||
        (type === 'PARTNERSHIP'
          ? 'Partnership inquiry'
          : type === 'VOLUNTEER'
            ? 'Get involved / volunteer'
            : ''),
    });
    this.showForm = true;
    setTimeout(() => {
      document.getElementById('inquiry-form')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 50);
  }

  openDonate() {
    this.isDonateOpen = true;
  }

  closeDonate() {
    this.isDonateOpen = false;
  }

  isInvalid(name: string): boolean {
    const c = this.inquiryForm.get(name);
    return !!c && c.invalid && c.touched;
  }

  onSubmit() {
    if (this.inquiryForm.invalid) {
      this.inquiryForm.markAllAsTouched();
      return;
    }

    const request: CreateInquiryRequest = {
      ...this.inquiryForm.value,
      status: 'NEW',
    };

    this.isSubmitting = true;
    this.api
      .invoke(createInquiry, { body: request })
      .then(() => {
        this.toastr.success('Message sent. We will get back to you soon.');
        this.inquiryForm.reset({ type: 'GENERAL' });
        this.showForm = false;
      })
      .catch((err) => {
        console.error(err);
        this.toastr.error(err?.error?.message || 'Failed to send message');
      })
      .finally(() => {
        this.isSubmitting = false;
        this.cdr.markForCheck();
      });
  }

  headerClass(color: string): string {
    switch (color) {
      case 'orange':
        return 'bg-orange';
      case 'green':
        return 'bg-green';
      default:
        return 'bg-primary';
    }
  }

  btnClass(color: string): string {
    switch (color) {
      case 'orange':
        return 'bg-orange hover:bg-orange-dark';
      case 'green':
        return 'bg-green hover:bg-green-dark';
      default:
        return 'bg-primary hover:bg-primary-light';
    }
  }
}