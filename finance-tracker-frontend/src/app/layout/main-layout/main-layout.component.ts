import { CommonModule } from '@angular/common';
import { Component, HostListener } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [RouterOutlet, RouterModule, CommonModule],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.scss'
})
export class MainLayoutComponent {

  isSidebarOpen = false;

  isUserMenuOpen = false;

  toggleUserMenu() {
    this.isUserMenuOpen = !this.isUserMenuOpen;
  }


  /** Returns true if current device width < 640px */
  isMobile(): boolean {
    return window.innerWidth < 640;
  }

  @HostListener('window:resize')
  onResize(): void {
    // Automatically close sidebar if resized to desktop
    if (!this.isMobile()) {
      this.isSidebarOpen = false;
    }
  }

  closeSidebarOnMobile(): void {
    if (this.isMobile()) {
      this.isSidebarOpen = false;
    }
  }


  closeUserMenu() {
    this.isUserMenuOpen = false;
  }

  logout() {
    alert('Logging out...');
    this.closeUserMenu();
  }

  @HostListener('document:click', ['$event'])
  @HostListener('document:touchstart', ['$event'])
  onDocumentClick(event: Event) {
    const target = event.target as HTMLElement;

    // Check if clicked inside toggle button or dropdown
    const clickedInsideButton = target.closest('#user-menu-button');
    const clickedInsideDropdown = target.closest('.dropdown-menu');

    if (!clickedInsideButton && !clickedInsideDropdown) {
      this.isUserMenuOpen = false;
    }
  }
}
