import { PortalView } from './portal';

/** Same catalogs as the static hubs, used until the API responds and if it is down. */
export const FALLBACK_PORTALS: Record<'ADMIN' | 'LEARNER', PortalView> = {
  ADMIN: {
    code: 'ADMIN',
    title: 'Admin Portal',
    subtitle: 'Access your essential administrative services in one place.',
    welcomeLabel: 'Welcome to',
    layout: 'admin',
    links: [
      { id: null, name: 'MIS Helpdesk', url: 'https://helpdesk.lpulaguna.com', description: 'Support and concerns', icon: 'MIS Helpdesk', downloadName: null, networkOnly: false, sortOrder: 10 },
      { id: null, name: 'LMS', url: 'https://lms.lpulaguna.edu.ph', description: 'Learning Management System', icon: 'LMS', downloadName: null, networkOnly: false, sortOrder: 20 },
      { id: null, name: 'Office 365', url: 'https://office.com', description: 'Email and productivity tools', icon: 'Office 365', downloadName: null, networkOnly: false, sortOrder: 30 },
      { id: null, name: 'Outlook', url: 'https://outlook.office.com/', description: 'Email and calendar', icon: 'Outlook', downloadName: null, networkOnly: false, sortOrder: 40 },
      { id: null, name: 'MS Teams', url: 'https://teams.microsoft.com/', description: 'Meetings and collaboration', icon: 'MS Teams', downloadName: null, networkOnly: false, sortOrder: 50 },
      { id: null, name: 'ERP', url: 'https://erp.lpulaguna.edu.ph', description: 'Enterprise resource planning', icon: 'ERP', downloadName: null, networkOnly: false, sortOrder: 60 },
      { id: null, name: 'Dorado (Payslips)', url: 'https://dorado.grantthorntonsolutions.ph/1.0.0.17871/Account/Login', description: 'Grant Thornton Dorado', icon: 'Dorado (Payslips)', downloadName: null, networkOnly: false, sortOrder: 70 },
      { id: null, name: 'Reservation System', url: 'https://reservation.lpulaguna.com', description: 'Book rooms and facilities', icon: 'Reservation System', downloadName: null, networkOnly: false, sortOrder: 80 },
      { id: null, name: 'Internet Access', url: 'http://web.lpu-laguna.edu.ph:8090', description: 'LPU Captive Portal', icon: 'Internet Access', downloadName: null, networkOnly: true, sortOrder: 90 },
    ],
  },
  LEARNER: {
    code: 'LEARNER',
    title: "Learner's Hub",
    subtitle: 'Access your essential student services in one place.',
    welcomeLabel: 'Welcome to',
    layout: 'learner',
    links: [
      { id: null, name: 'MIS Helpdesk', url: 'https://helpdesk.lpulaguna.com', description: 'Support and concerns', icon: 'MIS Helpdesk', downloadName: null, networkOnly: false, sortOrder: 10 },
      { id: null, name: 'LMS', url: 'https://lms.lpulaguna.edu.ph', description: 'Learning Management System', icon: 'LMS', downloadName: null, networkOnly: false, sortOrder: 20 },
      { id: null, name: 'Office 365', url: 'https://office.com', description: 'Email and productivity tools', icon: 'Office 365', downloadName: null, networkOnly: false, sortOrder: 30 },
      { id: null, name: 'Outlook', url: 'https://outlook.office.com/', description: 'Email and calendar', icon: 'Outlook', downloadName: null, networkOnly: false, sortOrder: 40 },
      { id: null, name: 'MS Teams', url: 'https://teams.microsoft.com/', description: 'Meetings and collaboration', icon: 'MS Teams', downloadName: null, networkOnly: false, sortOrder: 50 },
      { id: null, name: 'Student Portal', url: 'https://students.lpulaguna.edu.ph/Student/Login.php', description: 'Grades, records, and profile', icon: 'Student Portal', downloadName: null, networkOnly: false, sortOrder: 60 },
      { id: null, name: 'Reservation System', url: 'https://learnerreservation.lpulaguna.com', description: 'Book rooms and facilities', icon: 'Reservation System', downloadName: null, networkOnly: false, sortOrder: 70 },
      { id: null, name: 'Internet Access', url: 'http://web.lpu-laguna.edu.ph:8090', description: 'LPU Captive Portal', icon: 'Internet Access', downloadName: null, networkOnly: true, sortOrder: 80 },
      { id: null, name: 'LIT', url: '/LIT_LPU_V5.apk', description: 'Download LIT Android app', icon: 'LIT', downloadName: 'LIT_LPU_V5.apk', networkOnly: false, sortOrder: 90 },
    ],
  },
};

export function fallbackPortal(code: 'ADMIN' | 'LEARNER'): PortalView {
  return structuredClone(FALLBACK_PORTALS[code]);
}
