import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-objectives-page',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './objectives-page.component.html',
})
export class ObjectivesPageComponent {
  readonly objectives = [
    {
      title: 'Prevention',
      color: 'primary',
      icon: 'shield',
      short:
        'Raising awareness on elder rights, disability inclusion, gender equality, and child protection.',
      details: [
        'Community awareness campaigns on rights and protection',
        'Disability inclusion and accessibility advocacy',
        'Gender equality and child protection education',
        'Early intervention and risk reduction programs',
      ],
    },
    {
      title: 'Response',
      color: 'orange',
      icon: 'response',
      short: 'Hotlines, crisis shelters, legal aid, and trauma counseling.',
      details: [
        'Emergency hotlines and referral pathways',
        'Safe shelters and crisis accommodation',
        'Legal aid for survivors of violence and abuse',
        'Trauma counseling and psychosocial support',
      ],
    },
    {
      title: 'Empowerment',
      color: 'green',
      icon: 'heart',
      short: 'Livelihood support, vocational training, and reintegration.',
      details: [
        'Livelihood and income-generating support',
        'Vocational and skills training',
        'Family and community reintegration',
        'Long-term accompaniment for survivors',
      ],
    },
    {
      title: 'Systemic Change',
      color: 'primary',
      icon: 'scales',
      short: 'Advocacy for rights, policy reform, and justice.',
      details: [
        'Policy advocacy with local and national stakeholders',
        'Rights-based campaigns and public engagement',
        'Partnerships for institutional accountability',
        'Research and documentation to inform reform',
      ],
    },
  ];

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

  softClass(color: string): string {
    switch (color) {
      case 'orange':
        return 'bg-orange/5 dark:bg-orange/10 border-orange/20';
      case 'green':
        return 'bg-green/5 dark:bg-green/10 border-green/20';
      default:
        return 'bg-primary/5 dark:bg-primary/10 border-primary/20';
    }
  }
}