import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import LendRequests from './LendRequests'; // Adjust the import path as necessary

describe('LendRequests Component', () => {
  test('submits accept request on confirmation', async () => {
    render(<LendRequests />);

    // Find the "Accept" button (might be asynchronous)
    const acceptButton = await screen.findByRole('button', { name: /Accept/i });
    
    // Click the "Accept" button
    fireEvent.click(acceptButton);

    // Check if the confirmation dialog opened
    expect(await screen.findByText('Confirm Action')).toBeInTheDocument();

    // Simulate clicking the confirm button in the dialog
    fireEvent.click(await screen.findByText('Confirm'));

    // Wait for the request to be submitted and response to be handled
    await waitFor(() => {
      // Assuming there's a message or UI change indicating success
      expect(screen.getByText('Request Accepted')).toBeInTheDocument();
    });
  });
});
