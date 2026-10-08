import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import {
  CreateImpactStoryRequest,
  ImpactStoryResponse,
  ProgramResponse,
} from '../../../services/models';
import { Api } from '../../../services/api';
import {
  createImpactStory,
  getPagesOfPrograms,
  updateImpactStory,
} from '../../../services/functions';
import { ActivatedRoute, Router } from '@angular/router';
import { SharedService } from '../../../shared/services/shared.service';
import { ToastrService } from 'ngx-toastr';
import { ButtonComponent } from '../../../shared/components/ui/button/button.component';
import { LabelComponent } from '../../../shared/components/form/label/label.component';
import { FileUploaderComponent } from '../../../shared/components/file-uploader/file-uploader.component';

@Component({
  selector: 'app-create-impact-story',
  standalone: true,
  imports: [CommonModule,FormsModule, ReactiveFormsModule, ButtonComponent, LabelComponent, FileUploaderComponent],
  templateUrl: './create-impact-story.component.html',
})
export class CreateImpactStoryComponent implements OnInit {
  createStoryForm: FormGroup;

  isEditMode = false;
  publicId: string | undefined;
  isLoading = false;


  // program search
  programSearchTerm:string | undefined;
  programList: ProgramResponse[] = [];
  selectedProgramName = '';

  constructor(
    private api: Api,
    private fb: FormBuilder,
    private activatedRoute: ActivatedRoute,
    private sharedService: SharedService,
    private toastr: ToastrService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    this.createStoryForm = this.fb.group({
      beneficiaryName: ['', Validators.required],
      age: [null, [Validators.required, Validators.min(1)]],
      gender: ['', Validators.required],
      location: ['', Validators.required],
      programId: ['', Validators.required],
      shortQuote: ['', Validators.required],
      fullStory: ['', Validators.required],
      remark: ['', Validators.required],
      status: ['DRAFT', Validators.required],
      imageUrl: [''],
      videoUrl: [''],
      displayOrder:[99,Validators.required]
    });
  }

  ngOnInit(): void {
    const editParam = this.activatedRoute.snapshot.queryParamMap.get('edit');
    this.isEditMode = editParam === 'true';

    if (this.isEditMode) {
      const story: ImpactStoryResponse = this.sharedService.selectedImpactStory;
      if (story) {
        this.publicId = story.publicId;
        this.selectedProgramName = story.program || '';
        this.createStoryForm.patchValue({
          beneficiaryName: story.beneficiaryName,
          age: story.age,
          gender: story.gender,
          location: story.location,
          programId: story.programId,
          shortQuote: story.shortQuote,
          fullStory: story.fullStory,
          remark: story.remark,
          status: story.status || 'DRAFT',
          imageUrl: story.imageUrl,
          videoUrl: story.videoUrl,
        });
      }
    }
  }

  // ─── Program search ────────────────────────────────────────
  onSearch() {
    const term = this.programSearchTerm;

    if (term && term.trim().length >= 2) {
      this.searchProgramByName(term.trim());
    } else {
      this.programList = [];
    }
  }

  searchProgramByName(name: string) {
    this.api
      .invoke(getPagesOfPrograms, {
        page: 0,
        size: 10,
        name: name,
      })
      .then((res) => {
        if (res.content) {
          console.log('Program search results:', res.content);
          this.programList = res.content;
        } else {
          this.programList = [];
        }
        this.cdr.markForCheck();
      })
      .catch((error) => {
        console.error(error);
        this.toastr.error('Failed to search programs');
      });
  }

  onSelectProgram(program: ProgramResponse) {
    this.createStoryForm.patchValue({ programId: program.publicId });
    this.selectedProgramName = program.name || '';
    this.programSearchTerm = program.name || '';
    this.programList = [];
  }

  clearSelectedProgram() {
    this.createStoryForm.patchValue({ programId: '' });
    this.selectedProgramName = '';
    this.programSearchTerm = '';
    this.programList = [];
  }

  // ─── Submit ────────────────────────────────────────────────
  onSubmit() {
    if (this.createStoryForm.invalid) {
      this.createStoryForm.markAllAsTouched();
      this.toastr.error('Please fill all required fields');
      return;
    }

    const request: CreateImpactStoryRequest = this.createStoryForm.value;
    this.isLoading = true;

    if (this.isEditMode && this.publicId) {
      this.updateImpactStory(this.publicId, request);
    } else {
      this.createImpactStory(request);
    }
  }

  createImpactStory(request: CreateImpactStoryRequest) {
    this.api
      .invoke(createImpactStory, { body: request })
      .then(() => {
        this.toastr.success('Impact story created successfully');
        this.router.navigate(['/admin', 'impact-stories']);
      })
      .catch((error) => {
        console.error(error);
        this.toastr.error(error?.error?.message || 'Failed to create impact story');
      })
      .finally(() => {
        this.isLoading = false;
        this.cdr.markForCheck();
      });
  }

  updateImpactStory(publicId: string, request: CreateImpactStoryRequest) {
    this.api
      .invoke(updateImpactStory, {
        'public-id': publicId,
        body: request,
      })
      .then(() => {
        this.toastr.success('Impact story updated successfully');
        this.router.navigate(['/admin', 'impact-stories']);
      })
      .catch((error) => {
        console.error(error);
        this.toastr.error(error?.error?.message || 'Failed to update impact story');
      })
      .finally(() => {
        this.isLoading = false;
        this.cdr.markForCheck();
      });
  }

  onImageUpload(imageUrl:string){
    if(imageUrl){
      this.createStoryForm.patchValue({imageUrl:imageUrl});
    }
  }

  backToList() {
    this.router.navigate(['/admin', 'impact-stories']);
  }

  isInvalid(controlName: string): boolean {
    const control = this.createStoryForm.get(controlName);
    return !!control && control.invalid && control.touched;
  }
}