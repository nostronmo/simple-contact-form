export interface ContactRequest {
  name: string;
  phone: string;
  content: string;
}

export interface ContactResponse {
  id: string;
  name: string;
  phone: string;
  content: string;
  status: ContactStatus;
}

export enum ContactStatus {
  SENT = 'SENT',
  NOT_SENT = 'NOT_SENT',
  ERROR = 'ERROR',
  MAIL_NOT_WORKING_SPRING = 'MAIL_NOT_WORKING_SPRING',
  MAIL_NOT_WORKING_PROVIDER = 'MAIL_NOT_WORKING_PROVIDER',
}
