export interface LoginRequest {
  user: string;
  password: string;
  
}

export interface AuthResponse {
  token: string;
  role: string;
  avatar?: string;
  albumCompleteReward?: boolean; 
  verify?: boolean;
  tutorialView?: boolean;
  countActive?: boolean;
}

export interface RegisterRequest {
  user: string;
  password: string;
  name: string;
  personalId: string;
  coutry: string;
  avatar: string;
  role: string;
  email: string;
}

