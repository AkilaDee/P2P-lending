import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import LendRequests from './LendRequests'; // Adjust the import path as necessary

describe('LendRequests Component', () => {
  test('submits accept request on confirmation', async () => {
    render(<LendRequests />);

    
    const acceptButton = await screen.findByRole('button', { name: /Accept/i });
    
    
    fireEvent.click(acceptButton);

    
    expect(await screen.findByText('Confirm Action')).toBeInTheDocument();

    
    fireEvent.click(await screen.findByText('Confirm'));

    
    await waitFor(() => {
    
      expect(screen.getByText('Request Accepted')).toBeInTheDocument();
    });
  });
});
