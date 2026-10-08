import { Component, computed, input } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-youtube-player',
  standalone: true,
  template: `
    <div
      class="w-full overflow-hidden rounded-2xl shadow-lg border border-gray-200 dark:border-white/10 bg-zinc-900"
      [class.max-w-7xl]="!fullWidth()"
      [class.mx-auto]="!fullWidth()"
    >
      @if (title()) {
        <div
          class="px-5 py-3 text-sm font-medium text-white/90 bg-primary/90 dark:bg-primary-dark"
        >
          {{ title() }}
        </div>
      }

      <!-- Wider & shorter than classic 16:9 -->
      <div class="relative w-full" [class]="aspectClass()">
        @if (safeVideoUrl()) {
          <iframe
            [src]="safeVideoUrl()!"
            [title]="title() || 'YouTube video player'"
            class="absolute inset-0 h-full w-full border-0"
            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
            allowfullscreen
            loading="lazy"
          ></iframe>
        } @else {
          <div class="flex h-full items-center justify-center text-zinc-400 text-sm">
            Invalid YouTube URL
          </div>
        }
      </div>
    </div>
  `,
})
export class YoutubePlayerComponent {
  /** Full YouTube URL (watch, youtu.be, embed, shorts) */
  url = input<string>('');

  /** Optional heading above the player */
  title = input<string>('');

  /** Use full container width (home banner style) */
  fullWidth = input<boolean>(true);

  /**
   * Aspect ratio:
   * - 'video'  → 16/9 (admin default)
   * - 'wide'   → 21/9 (home – wider & shorter)
   * - 'banner' → 2.5/1 (even shorter strip)
   */
  aspect = input<'video' | 'wide' | 'banner'>('wide');

  constructor(private sanitizer: DomSanitizer) {}

  safeVideoUrl = computed<SafeResourceUrl | null>(() => {
    const videoId = this.extractId(this.url());
    if (!videoId) return null;
    const embedUrl = `https://www.youtube.com/embed/${videoId}?rel=0`;
    return this.sanitizer.bypassSecurityTrustResourceUrl(embedUrl);
  });

  aspectClass = computed(() => {
    switch (this.aspect()) {
      case 'banner':
        return 'aspect-[2.5/1] min-h-[200px]';
      case 'wide':
        return 'aspect-[21/9] min-h-[220px]';
      default:
        return 'aspect-video';
    }
  });

  private extractId(url: string): string | null {
    if (!url) return null;
    const regExp =
      /^.*(youtu.be\/|v\/|u\/\w\/|embed\/|shorts\/|watch\?v=|&v=)([^#&?]*).*/;
    const match = url.match(regExp);
    return match && match[2].length === 11 ? match[2] : null;
  }
}