import { ChangeDetectorRef, Component } from '@angular/core';
import { BadgeComponent } from '../../ui/badge/badge.component';
import { SafeHtmlPipe } from '../../../pipe/safe-html.pipe';
import { Api } from '../../../../services/api';
import { getImpactStatistics } from '../../../../services/functions';
import { ImpactStatisticsResponse } from '../../../../services/models';
import { SelectComponent } from '../../form/select/select.component';

@Component({
  selector: 'app-ecommerce-metrics',
  imports: [SafeHtmlPipe, SelectComponent],
  templateUrl: './ecommerce-metrics.component.html'
})
export class EcommerceMetricsComponent {

  public icons = {
    groupIcon:`<svg width="1em" height="1em" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" class="text-gray-800 size-6 dark:text-white/90"><path fill-rule="evenodd" clip-rule="evenodd" d="M8.80443 5.60156C7.59109 5.60156 6.60749 6.58517 6.60749 7.79851C6.60749 9.01185 7.59109 9.99545 8.80443 9.99545C10.0178 9.99545 11.0014 9.01185 11.0014 7.79851C11.0014 6.58517 10.0178 5.60156 8.80443 5.60156ZM5.10749 7.79851C5.10749 5.75674 6.76267 4.10156 8.80443 4.10156C10.8462 4.10156 12.5014 5.75674 12.5014 7.79851C12.5014 9.84027 10.8462 11.4955 8.80443 11.4955C6.76267 11.4955 5.10749 9.84027 5.10749 7.79851ZM4.86252 15.3208C4.08769 16.0881 3.70377 17.0608 3.51705 17.8611C3.48384 18.0034 3.5211 18.1175 3.60712 18.2112C3.70161 18.3141 3.86659 18.3987 4.07591 18.3987H13.4249C13.6343 18.3987 13.7992 18.3141 13.8937 18.2112C13.9797 18.1175 14.017 18.0034 13.9838 17.8611C13.7971 17.0608 13.4132 16.0881 12.6383 15.3208C11.8821 14.572 10.6899 13.955 8.75042 13.955C6.81096 13.955 5.61877 14.572 4.86252 15.3208ZM3.8071 14.2549C4.87163 13.2009 6.45602 12.455 8.75042 12.455C11.0448 12.455 12.6292 13.2009 13.6937 14.2549C14.7397 15.2906 15.2207 16.5607 15.4446 17.5202C15.7658 18.8971 14.6071 19.8987 13.4249 19.8987H4.07591C2.89369 19.8987 1.73504 18.8971 2.05628 17.5202C2.28015 16.5607 2.76117 15.2906 3.8071 14.2549ZM15.3042 11.4955C14.4702 11.4955 13.7006 11.2193 13.0821 10.7533C13.3742 10.3314 13.6054 9.86419 13.7632 9.36432C14.1597 9.75463 14.7039 9.99545 15.3042 9.99545C16.5176 9.99545 17.5012 9.01185 17.5012 7.79851C17.5012 6.58517 16.5176 5.60156 15.3042 5.60156C14.7039 5.60156 14.1597 5.84239 13.7632 6.23271C13.6054 5.73284 13.3741 5.26561 13.082 4.84371C13.7006 4.37777 14.4702 4.10156 15.3042 4.10156C17.346 4.10156 19.0012 5.75674 19.0012 7.79851C19.0012 9.84027 17.346 11.4955 15.3042 11.4955ZM19.9248 19.8987H16.3901C16.7014 19.4736 16.9159 18.969 16.9827 18.3987H19.9248C20.1341 18.3987 20.2991 18.3141 20.3936 18.2112C20.4796 18.1175 20.5169 18.0034 20.4837 17.861C20.2969 17.0607 19.913 16.088 19.1382 15.3208C18.4047 14.5945 17.261 13.9921 15.4231 13.9566C15.2232 13.6945 14.9995 13.437 14.7491 13.1891C14.5144 12.9566 14.262 12.7384 13.9916 12.5362C14.3853 12.4831 14.8044 12.4549 15.2503 12.4549C17.5447 12.4549 19.1291 13.2008 20.1936 14.2549C21.2395 15.2906 21.7206 16.5607 21.9444 17.5202C22.2657 18.8971 21.107 19.8987 19.9248 19.8987Z" fill="currentColor"></path></svg>`,
    childIcon:`<svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 640 640"><!-- Icon from Font Awesome Solid by Dave Gandy - https://creativecommons.org/licenses/by/4.0/ --><path fill="#888888" d="M384 128c0-35.3-28.7-64-64-64s-64 28.7-64 64s28.7 64 64 64s64-28.7 64-64M280.9 233.3c-23.7-8.4-44.5-24.3-58.8-45.8l-19.5-29.3c-9.8-14.7-29.6-18.6-44.3-8.8s-18.7 29.6-8.9 44.4l19.5 29.2c18.1 27.1 42.8 48.4 71.1 62.4V544c0 17.7 14.3 32 32 32s32-14.3 32-32v-96h32v96c0 17.7 14.3 32 32 32s32-14.3 32-32V285.6c29.1-14.2 54.4-36.2 72.7-64.2l18.2-27.9c9.6-14.8 5.4-34.6-9.4-44.3s-34.6-5.5-44.3 9.4L419 186.4c-21.8 33.4-58.9 53.6-98.8 53.6c-12.6 0-24.9-2-36.6-5.8c-.9-.3-1.8-.7-2.7-.9"/></svg>`,
    womenIcon:`<svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 48 48"><!-- Icon from IconPark Solid by ByteDance - https://github.com/bytedance/IconPark/blob/master/LICENSE --><path fill="#888888" stroke="#888888" stroke-linejoin="round" stroke-width="4" d="M19.225 6.37c-3.183 0-8.063 1.837-6.366 13.213q.04.258.095.557c.31 1.711.788 4.36-2.254 6.544c2.069 1.698 5.016 3.395 8.597 4.13a28 28 0 0 1-.532 2.414c-.291 1.068-.901 1.825-1.991 2.148c-.96.284-1.966.514-2.996.75q-.398.09-.8.184c-1.285.299-2.595.63-3.756 1.098c-1.164.47-2.246 1.103-3.028 2.047c-.796.963-1.222 2.177-1.193 3.695c.01.472.414.85.91.85h36.178c.496 0 .9-.378.91-.85c.029-1.518-.396-2.732-1.193-3.695c-.781-.944-1.864-1.578-3.027-2.047c-1.162-.469-2.472-.8-3.758-1.099l-.798-.183c-1.031-.236-2.037-.466-2.997-.75c-1.09-.323-1.7-1.08-1.991-2.148a27 27 0 0 1-.606-2.88c2.738-.824 5.648-2.305 8.671-4.67l-.014-.014c-1.419-1.358-4.079-3.904-4.268-9.948c0-8.322-5.306-15.637-13.793-9.347Z"/></svg>`,
    specialNeedsIcon:`<svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24"><!-- Icon from MingCute Icon by MingCute Design - https://github.com/Richard9394/MingCute/blob/main/LICENSE --><g fill="none" fill-rule="evenodd"><path d="m12.594 23.258l-.012.002l-.071.035l-.02.004l-.014-.004l-.071-.036q-.016-.004-.024.006l-.004.01l-.017.428l.005.02l.01.013l.104.074l.015.004l.012-.004l.104-.074l.012-.016l.004-.017l-.017-.427q-.004-.016-.016-.018m.264-.113l-.014.002l-.184.093l-.01.01l-.003.011l.018.43l.005.012l.008.008l.201.092q.019.005.029-.008l.004-.014l-.034-.614q-.005-.019-.02-.022m-.715.002a.02.02 0 0 0-.027.006l-.006.014l-.034.614q.001.018.017.024l.015-.002l.201-.093l.01-.008l.003-.011l.018-.43l-.003-.012l-.01-.01z"/><path fill="#888888" d="M11 4a1 1 0 1 0 0 2a1 1 0 0 0 0-2M8 5a3 3 0 1 1 4 2.83v1.45l2.162.72H16a1 1 0 1 1 0 2h-2a1 1 0 0 1-.316-.051L12 11.387V13a1 1 0 0 0 1 1h2a3 3 0 0 1 2.4 1.2l2.1 2.8h.5a1 1 0 1 1 0 2h-1a1 1 0 0 1-.8-.4l-2.4-3.2a1 1 0 0 0-.8-.4h-2a3 3 0 0 1-3-3V7.829A3 3 0 0 1 8 5m.272 6.17a4.5 4.5 0 1 0 5.238 6.375l1.78.91a6.5 6.5 0 1 1-7.563-9.21z"/></g></svg>`
  }

  branchId:string | undefined;
  lastDays:number | undefined;

  impactStatics:ImpactStatisticsResponse | undefined;

  lastDaysOptions = [
    { value: 'ALL', label: 'ALL' },
    { value: '7', label: 'Last 7 Days' },
    { value: '30', label: 'Last 30 Days' },
    { value: '90', label: 'Last 3 Months' },
    { value: '180', label: 'Last 6 Months' },
    { value: '365', label: 'Last 1 Year' },
  ];

  constructor(
    private api:Api,
    private cd:ChangeDetectorRef
  ){}

  ngOnInit(): void {
    this.loadImpactStatistics();
  }

  // load impact statistics
  loadImpactStatistics(){
    this.api.invoke(getImpactStatistics,{
      'lastDays': this.lastDays,
      'year': undefined,
    }).then((res)=>{
      this.impactStatics = res;
      this.cd.detectChanges();
    }).catch((err)=>{
      console.log(err);
    })
  }

  handleLastDaysChange(lastDays:string){
    if(lastDays && lastDays === 'ALL'){
      this.lastDays = undefined;
      this.loadImpactStatistics();
    } else if(lastDays){
      this.lastDays = Number(lastDays);
      this.loadImpactStatistics();
    }

  }
}
