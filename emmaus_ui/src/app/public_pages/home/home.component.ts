import { Component, OnInit, ChangeDetectorRef, AfterViewInit, OnDestroy, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Api } from '../../services/api';
import {
  getPagesOfPrograms,
  getPagesOfImpactStories,
  getPagesOfDocuments,
  getPagesOfStaffProfile,
  getImpactStatistics,
} from '../../services/functions';
import {
  ProgramResponse,
  ImpactStoryResponse,
  DocumentResponse,
  StaffProfileResponse,
  ImpactStatisticsResponse,
} from '../../services/models';
import { FileReaderComponent } from '../../shared/components/file-reader/file-reader.component';
import { YoutubePlayerComponent } from '../../shared/components/youtube-player/youtube-player.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, FileReaderComponent, YoutubePlayerComponent],
  templateUrl: './home.component.html',
})
export class HomeComponent implements OnInit,AfterViewInit, OnDestroy {
  @ViewChild('statsSection') statsSection!: ElementRef<HTMLElement>;

  videoUrl: string = 'https://youtu.be/SAv_9AcUubI?list=RDSAv_9AcUubI';

  programs: ProgramResponse[] = [];
  stories: ImpactStoryResponse[] = [];
  documents: DocumentResponse[] = [];
  founders: StaffProfileResponse[] = [];
  impactStatics:ImpactStatisticsResponse | undefined;

  isLoadingPrograms = true;
  isLoadingStories = true;
  isLoadingDocuments = true;
  isLoadingFounders = true;

  readonly objectives = [
    {
      title: 'Prevention',
      description:
        'Raising awareness on elder rights, disability inclusion, gender equality, and child protection.',
      icon: 'shield',
      color: 'sky-blue',
    },
    {
      title: 'Response',
      description: 'Hotlines, crisis shelters, legal aid, and trauma counseling.',
      icon: 'response',
      color: 'orange',
    },
    {
      title: 'Empowerment',
      description: 'Livelihood support, vocational training, and reintegration.',
      icon: 'heart',
      color: 'green',
    },
    {
      title: 'Systemic Change',
      description: 'Advocacy for rights, policy reform, and justice.',
      icon: 'scales',
      color: 'primary',
    },
  ];

  // Display values that animate
  displayStats = {
    elderly: 0,
    women: 0,
    children: 0,
    specialNeeds: 0,
    total: 0,
  };

  private hasAnimated = false;
  private statsObserver?: IntersectionObserver;
  private pendingTargets: typeof this.displayStats | null = null;

  readonly partnerLogos = [
    { name: 'UNICEF', src: '/images/partners/unicef.png'},
    { name: 'CARE', src: '/images/partners/care.png'},
    { name: 'Plan', src: '/images/partners/plan.png'},
    // add more as needed
  ];

  constructor(
    private api: Api,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadPrograms();
    this.loadStories();
    this.loadDocuments();
    this.loadFounders();
    this.loadImpactStatistics();
  }

  ngAfterViewInit(): void {
    this.setupStatsObserver();
  }

  ngOnDestroy(): void {
    this.statsObserver?.disconnect();
  }

  loadPrograms() {
    this.isLoadingPrograms = true;
    this.api
      .invoke(getPagesOfPrograms, { page: 0, size: 3 })
      .then((res) => {
        this.programs = res?.content ?? [];
      })
      .catch(() => {
        this.programs = [];
      })
      .finally(() => {
        this.isLoadingPrograms = false;
        this.cdr.markForCheck();
      });
  }

  loadStories() {
    this.isLoadingStories = true;
    this.api
      .invoke(getPagesOfImpactStories, { page: 0, size: 3 })
      .then((res) => {
        this.stories = res?.content ?? [];
      })
      .catch(() => {
        this.stories = [];
      })
      .finally(() => {
        this.isLoadingStories = false;
        this.cdr.markForCheck();
      });
  }

  loadDocuments() {
    this.isLoadingDocuments = true;
    this.api
      .invoke(getPagesOfDocuments, { page: 0, size: 4 })
      .then((res) => {
        this.documents = res?.content ?? [];
      })
      .catch(() => {
        this.documents = [];
      })
      .finally(() => {
        this.isLoadingDocuments = false;
        this.cdr.markForCheck();
      });
  }

  /**
   * Founders / leadership from staff profiles.
   * Adjust the filter param to match your API
   * (e.g. role, department, or a dedicated "founder" flag).
   */
  loadFounders() {
    this.isLoadingFounders = true;
    this.api
      .invoke(getPagesOfStaffProfile, {
        page: 0,
        size: 4,
        position: 'Founder', 
        department: 'Board'
      })
      .then((res) => {
        this.founders = res?.content ?? [];
      })
      .catch(() => {
        this.founders = [];
      })
      .finally(() => {
        this.isLoadingFounders = false;
        this.cdr.markForCheck();
      });
  }

  // ─── Load stats (store targets only; don't animate yet) ───
  loadImpactStatistics() {
    this.api
      .invoke(getImpactStatistics, {})
      .then((res) => {
        this.impactStatics = res; // adjust if nested (res.data etc.)
        this.pendingTargets = {
          // elderly:100000,
          elderly: res?.elderlyImpacts ?? 0,
          women: res?.womenImpacts ?? 0,
          children: res?.childImpacts ?? 0,
          specialNeeds: res?.specialNeedsImpacts ?? 0,
          total: res?.totalImpacts ?? 0,
        };
        // If section is already visible when data arrives, animate now
        this.tryAnimateStats();
      })
      .catch(() => {
        this.pendingTargets = null;
      })
      .finally(() => this.cdr.markForCheck());
  }

  // ─── Observer ─────────────────────────────────────────────
  private setupStatsObserver() {
    if (!this.statsSection?.nativeElement) return;

    this.statsObserver = new IntersectionObserver(
      (entries) => {
        const entry = entries[0];
        if (entry?.isIntersecting) {
          this.tryAnimateStats();
        }
      },
      {
        threshold: 0.35, // ~35% of section visible
        rootMargin: '0px 0px -40px 0px',
      }
    );

    this.statsObserver.observe(this.statsSection.nativeElement);
  }

  /** Animate only once, when we have data AND section is (or becomes) visible */
  private tryAnimateStats() {
    if (this.hasAnimated || !this.pendingTargets) return;

    // Double-check visibility in case data arrived first
    const el = this.statsSection?.nativeElement;
    if (!el) return;

    const rect = el.getBoundingClientRect();
    const inView =
      rect.top < window.innerHeight * 0.85 && rect.bottom > window.innerHeight * 0.15;

    if (!inView) return;

    this.hasAnimated = true;
    this.statsObserver?.disconnect();
    this.runCountUp(this.pendingTargets);
  }

  private runCountUp(targets: typeof this.displayStats) {
    const duration = 1600;
    const start = performance.now();

    const tick = (now: number) => {
      const progress = Math.min((now - start) / duration, 1);
      const eased = 1 - Math.pow(1 - progress, 3); // ease-out

      this.displayStats = {
        elderly: Math.floor(targets.elderly * eased),
        women: Math.floor(targets.women * eased),
        children: Math.floor(targets.children * eased),
        specialNeeds: Math.floor(targets.specialNeeds * eased),
        total: Math.floor(targets.total * eased),
      };
      this.cdr.markForCheck();

      if (progress < 1) {
        requestAnimationFrame(tick);
      } else {
        this.displayStats = { ...targets };
        this.cdr.markForCheck();
      }
    };

    requestAnimationFrame(tick);
  }


  // Call this after impactStatics is loaded successfully
  private animateStats() {
    if (!this.impactStatics) return;

    const targets = {
      // elderly: this.impactStatics.elderlyImpacts ?? 0,
      elderly: 1000000,
      women: this.impactStatics.womenImpacts ?? 0,
      children: this.impactStatics.childImpacts ?? 0,
      specialNeeds: this.impactStatics.specialNeedsImpacts ?? 0,
      total: this.impactStatics.totalImpacts ?? 0,
    };

    const duration = 1500; // ms
    const start = performance.now();

    const tick = (now: number) => {
      const progress = Math.min((now - start) / duration, 1);
      // ease-out
      const eased = 1 - Math.pow(1 - progress, 3);

      this.displayStats = {
        elderly: Math.floor(targets.elderly * eased),
        women: Math.floor(targets.women * eased),
        children: Math.floor(targets.children * eased),
        specialNeeds: Math.floor(targets.specialNeeds * eased),
        total: Math.floor(targets.total * eased),
      };
      this.cdr.markForCheck();

      if (progress < 1) {
        requestAnimationFrame(tick);
      } else {
        this.displayStats = { ...targets };
        this.cdr.markForCheck();
      }
    };

    requestAnimationFrame(tick);
  }

  getObjectiveHeaderClass(color: string): string {
    switch (color) {
      case 'orange':
        return 'bg-orange';
      case 'green':
        return 'bg-green';
      default:
        return 'bg-primary';
    }
  }


  getStoryHeaderClass(index: number): string {
    const classes = ['bg-primary', 'bg-orange', 'bg-green'];
    return classes[index % 3];
  }


  /** Cycle: primary → orange → green */
  getAccentClass(index: number): string {
    const accents = ['bg-primary', 'bg-orange', 'bg-green'];
    return accents[index % 3];
  }

  getAccentTextClass(index: number): string {
    const accents = ['text-primary', 'text-orange', 'text-green'];
    return accents[index % 3];
  }

getAccentSoftClass(index: number): string {
  const accents = [
    'bg-primary/5 dark:bg-primary/10',
    'bg-orange/5 dark:bg-orange/10',
    'bg-green/5 dark:bg-green/10',
  ];
  return accents[index % 3];
}

getObjectiveBtnClass(color: string): string {
  switch (color) {
    case 'orange':
      return 'bg-orange hover:bg-orange-dark';
    case 'green':
      return 'bg-green hover:bg-green-dark';
     case 'sky-blue':
        return 'bg-sky-600 hover:bg-sky-700';  
    default:
      return 'bg-primary hover:bg-primary-light';
  }
}

  // Keep for documents if you still use 4 colors
  getDocAccentClass(index: number): string {
    const accents = ['bg-primary', 'bg-orange', 'bg-green', 'bg-primary'];
    return accents[index % 4];
  }
}