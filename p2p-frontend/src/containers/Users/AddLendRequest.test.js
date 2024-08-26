import React, { useState } from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import axios from 'axios';
import AddLendRequest from './AddLendRequest';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const backendUrl = 'http://mocked-backend-url.com'; // Mocked backend URL

// Mock axios
jest.mock('axios');

describe('AddLendRequest Component', () => {
  beforeEach(() => {
    jest.clearAllMocks(); // Clear all mocks before each test
  });

  test('renders AddLendRequest component correctly', () => {
    render(<AddLendRequest />);

    // Check if the Add New Lend Request button is rendered
    expect(screen.getByText('Add New Lend Request')).toBeInTheDocument();

    // Check if the Pending Lend Requests header is rendered
    expect(screen.getByText('Pending Lend Requests')).toBeInTheDocument();
  });

  test('opens the dialog when Add New Lend Request button is clicked', () => {
    render(<AddLendRequest />);

    // Click the Add New Lend Request button
    fireEvent.click(screen.getByText('Add New Lend Request'));

    // Check if the dialog opened
    expect(screen.getByText('Add New Loan Request')).toBeInTheDocument();
  });

  test('validates form input before submitting', async () => {
    render(<AddLendRequest />);

    // Open the dialog
    fireEvent.click(screen.getByText('Add New Lend Request'));

    // Click the Submit button without entering any data
    fireEvent.click(screen.getByText('Submit'));

    // Check for validation errors
    expect(await screen.findByText('All fields are required!')).toBeInTheDocument();
  });

  test('makes an API call to submit a lend request', async () => {
    const mockPost = jest.fn();
    axios.post.mockImplementation(mockPost);

    render(<AddLendRequest />);

    // Open the dialog
    fireEvent.click(screen.getByText('Add New Lend Request'));

    // Fill out the form
    fireEvent.change(screen.getByLabelText('Amount'), { target: { value: '1000' } });
    fireEvent.change(screen.getByLabelText('Interest Rate (%)'), { target: { value: '5' } });
    fireEvent.change(screen.getByLabelText('Repayment Period (months)'), { target: { value: '12' } });

    // Submit the form
    fireEvent.click(screen.getByText('Submit'));

    // Check if the API call was made with the correct data
    await waitFor(() => {
      expect(mockPost).toHaveBeenCalledWith(`${backendUrl}/users/lendrequests/submit`, expect.objectContaining({
        amount: '1000',
        interestRate: '5',
        repaymentPeriod: '12',
      }));
    });
  });

  test('displays a toast notification on error', async () => {
    axios.post.mockRejectedValueOnce(new Error('Error creating lend request'));

    render(
      <>
        <AddLendRequest />
        <ToastContainer />
      </>
    );

    // Open the dialog
    fireEvent.click(screen.getByText('Add New Lend Request'));

    // Fill out the form
    fireEvent.change(screen.getByLabelText('Amount'), { target: { value: '1000' } });
    fireEvent.change(screen.getByLabelText('Interest Rate (%)'), { target: { value: '5' } });
    fireEvent.change(screen.getByLabelText('Repayment Period (months)'), { target: { value: '12' } });

    // Submit the form
    fireEvent.click(screen.getByText('Submit'));

    // Check for the error toast notification
    await waitFor(() => {
      expect(screen.getByText('There was an error creating the lend request!')).toBeInTheDocument();
    });
  });
});