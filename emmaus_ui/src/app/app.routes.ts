import { Routes } from '@angular/router';
import { EcommerceComponent } from './pages/dashboard/ecommerce/ecommerce.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { FormElementsComponent } from './pages/forms/form-elements/form-elements.component';
import { BasicTablesComponent } from './pages/tables/basic-tables/basic-tables.component';
import { BlankComponent } from './pages/blank/blank.component';
import { NotFoundComponent } from './pages/other-page/not-found/not-found.component';
import { AppLayoutComponent } from './shared/layout/app-layout/app-layout.component';
import { InvoicesComponent } from './pages/invoices/invoices.component';
import { LineChartComponent } from './pages/charts/line-chart/line-chart.component';
import { BarChartComponent } from './pages/charts/bar-chart/bar-chart.component';
import { AlertsComponent } from './pages/ui-elements/alerts/alerts.component';
import { AvatarElementComponent } from './pages/ui-elements/avatar-element/avatar-element.component';
import { BadgesComponent } from './pages/ui-elements/badges/badges.component';
import { ButtonsComponent } from './pages/ui-elements/buttons/buttons.component';
import { ImagesComponent } from './pages/ui-elements/images/images.component';
import { VideosComponent } from './pages/ui-elements/videos/videos.component';
import { SignInComponent } from './pages/auth-pages/sign-in/sign-in.component';
import { SignUpComponent } from './pages/auth-pages/sign-up/sign-up.component';
import { CalenderComponent } from './pages/calender/calender.component';
import { ChangePasswordComponent } from './pages/change-password/change-password.component';
import { ForgetPasswordComponent } from './pages/auth-pages/forget-password/forget-password.component';
import { ChangeTempPasswordComponent } from './pages/auth-pages/change-temp-password/change-temp-password.component';
import { ResetPasswordComponent } from './pages/auth-pages/reset-password/reset-password.component';
import { authGuard } from './services/auth-guard/auth.guard';
import { ResetEmailComponent } from './pages/auth-pages/reset-email/reset-email.component';
import { HomeComponent } from './public_pages/home/home.component';
import { StaffProfileComponent } from './pages/staff-profiles/staff-profile/staff-profile.component';
import { CreateStaffProfileComponent } from './pages/staff-profiles/create-staff-profile/create-staff-profile.component';
import { DocumentComponent } from './pages/document/document.component';
import { ProgramsComponent } from './pages/programs/programs.component';
import { ImpactStoriesComponent } from './pages/impact-story/impact-stories/impact-stories.component';
import { CreateImpactStoryComponent } from './pages/impact-story/create-impact-story/create-impact-story.component';
import { InquiresComponent } from './pages/inquires/inquires.component';
import { PublicLayoutComponent } from './public_pages/public-layout/public-layout.component';
import { ProgramsPageComponent } from './public_pages/programs-page/programs-page.component';
import { ImpactStoriesPageComponent } from './public_pages/impact-stories-page/impact-stories-page.component';
import { ImpactStoryDetailComponent } from './public_pages/impact-story-detail/impact-story-detail.component';
import { ResourcesPageComponent } from './public_pages/resources-page/resources-page.component';

export const routes: Routes = [
  // app.routes.ts (public part)
{
  path: '',
  component: PublicLayoutComponent,
  children: [
    { 
      path: '',
      component:HomeComponent,
      title: 'Emmaus Campanions'
     },
     { 
      path: 'programs',
      component:ProgramsPageComponent,
      title: 'Programs'
     },
    { 
      path: 'impact-stories',
      component:ImpactStoriesPageComponent,
      title: 'Impact Stories'
     },
     { 
      path: 'impact-stories/:id',
      component:ImpactStoryDetailComponent,
      title: 'Impact Stories'
     },
     { 
      path: 'resources',
      component:ResourcesPageComponent,
      title: 'Resources'
     },


  ],
  },
  {
    path:'admin',
    component:AppLayoutComponent,
    canActivate:[authGuard],
    children:[
      {
        path: '',
        component: EcommerceComponent,
        pathMatch: 'full',
        title:
          'Emmaus Dashboard',
      },
      //change password
      {
        path:'change-password',
        component:ChangePasswordComponent,
        title:'Change Password',
      },
      {
        path:'profile',
        component:ProfileComponent,
        title:'Profile'
      },
      //staff-profile
      {
        path:'staff-profile',
        component:StaffProfileComponent,
        title:' Emmaus Staff Profile'
      },
      //create-staff-profile
      {
        path:'create-staff-profile',
        component:CreateStaffProfileComponent,
        title:'Create Staff Profile'
      },
      // document
      {
        path:'document',
        component:DocumentComponent,
        title:'Emmaus Document'
      },
      // program
      {
        path:'programs',
        component:ProgramsComponent,
        title:'Emmaus Programs'
      },
      // story
      {
        path:'impact-stories',
        component:ImpactStoriesComponent,
        title:'Emmaus Impact Stories'
      },
      // create story
      {
        path:'create-impact-story',
        component:CreateImpactStoryComponent,
        title:'Emmaus Create Impact Story'
      },
      // inquires
      {
        path:'inquires',
        component:InquiresComponent,
        title:'Emmaus Inquires'
      }
    ]
  },
  // auth pages
  {
    path:'signin',
    component:SignInComponent,
    title:'Emmaus Sign In '
  },
  {
    path:'signup',
    component:SignUpComponent,
    title:'Emmaus Sign Up '
  },
  {
    path:'forget-password',
    component:ForgetPasswordComponent,
    title:'Emmaus Forget Password'
  },
  {
    path:'reset-password',
    component:ResetPasswordComponent,
    title:'Emmaus Reset Password'
  },
  {
    path:'reset-email',
    component:ResetEmailComponent,
    title:'Emmaus Reset Email'
  },
  {
    path:'change-temp-password',
    component:ChangeTempPasswordComponent,
    title:'Emmaus Change Temporary Password'
  },
  // error pages
  {
    path:'**',
    component:NotFoundComponent,
    title:'Emmaus Not Found'
  },
];
