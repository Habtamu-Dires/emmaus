import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";
import { ImpactStoryResponse, StaffProfileResponse } from "../../services/models";

@Injectable({
  providedIn: 'root'
})
export class SharedService {
    
    private selectedProfile = new BehaviorSubject<StaffProfileResponse>({});
    private selectedStory = new BehaviorSubject<ImpactStoryResponse>({});

    /** Observable for staffProfile  */
    selectedProfile$: Observable<StaffProfileResponse> = this.selectedProfile.asObservable();

    /** Observable for impact story  */
    selectedStory$: Observable<ImpactStoryResponse> = this.selectedStory.asObservable();

    
    //_______________staff profile ____________________
    // set staffProfile 
    setStaffProfile(staffProfile: StaffProfileResponse): void {
      this.selectedProfile.next(staffProfile);
    }

    /** Get current staffProfile  synchronously */
    get selectedStaffProfile(): StaffProfileResponse {
      return this.selectedProfile.value;
    }

    //________________ impact story ____________________

    setImpactStory(story: ImpactStoryResponse): void {
      this.selectedStory.next(story);
    }

    get selectedImpactStory(): ImpactStoryResponse {
      return this.selectedStory.value;
    }



}