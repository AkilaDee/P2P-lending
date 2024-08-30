import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import axios from 'axios';
import LendRequests from './LendRequests.js';
import { BrowserRouter as Router } from 'react-router-dom';
import { backendUrl } from '../../UrlConfig.js';
import '@testing-library/jest-dom/extend-expect'; 
// Mock axios
jest.mock('axios');

beforeEach(() => {
  jest.clearAllMocks();
  const mockUser = JSON.stringify({ userId: 1, firstName: 'Test', lastName: 'User' });
  window.localStorage.setItem('user', mockUser);
});

afterEach(() => {
  window.localStorage.clear();
});

describe('LendRequests Component', () => {
  test('renders LendRequests component correctly', async () => {
    axios.post.mockResolvedValueOnce({
      data: [
        {
          lendRequestId: 1,
          createdAt: '2024-08-01',
          amount: 1000,
          interestRate: 5,
          repaymentPeriod: 12,
          total: 1050,
          requestedByFirstName: 'John',
          requestedByLastName: 'Doe',
          userId: 1,
        },
      ],
    });

    render(
      <Router>
        <LendRequests />
      </Router>
    );

    expect(screen.getByText('Lend Requests')).toBeInTheDocument();

    await waitFor(() => {
      expect(screen.getByText('2024-08-01')).toBeInTheDocument();
    });

    expect(screen.getByText('John Doe')).toBeInTheDocument();
    expect(screen.getByText('1000')).toBeInTheDocument();
    expect(screen.getByText('5')).toBeInTheDocument();
    expect(screen.getByText('12')).toBeInTheDocument();
    expect(screen.getByText('1050')).toBeInTheDocument();
  });

  test('filters data based on search term', async () => {
    axios.post.mockResolvedValueOnce({
      data: [
        {
          lendRequestId: 1,
          createdAt: '2024-08-01',
          amount: 1000,
          interestRate: 5,
          repaymentPeriod: 12,
          total: 1050,
          requestedByFirstName: 'John',
          requestedByLastName: 'Doe',
          userId: 1,
        },
        {
          lendRequestId: 2,
          createdAt: '2024-08-02',
          amount: 2000,
          interestRate: 4,
          repaymentPeriod: 24,
          total: 2080,
          requestedByFirstName: 'Jane',
          requestedByLastName: 'Smith',
          userId: 2,
        },
      ],
    });

    render(
      <Router>
        <LendRequests />
      </Router>
    );

    await waitFor(() => {
      expect(screen.getByText('2024-08-01')).toBeInTheDocument();
    });

    fireEvent.change(screen.getByPlaceholderText('Search...'), { target: { value: 'Jane' } });

    expect(screen.queryByText('John Doe')).not.toBeInTheDocument();
    expect(screen.getByText('Jane Smith')).toBeInTheDocument();
  });

  // test('opens and closes confirmation dialog', async () => {
  //   axios.post.mockResolvedValueOnce({
  //     data: [
  //       {
  //         lendRequestId: 1,
  //         createdAt: '2024-08-01',
  //         amount: 1000,
  //         interestRate: 5,
  //         repaymentPeriod: 12,
  //         total: 1050,
  //         requestedByFirstName: 'John',
  //         requestedByLastName: 'Doe',
  //         userId: 1,
  //       },
  //     ],
  //   });

  //   render(
  //     <Router>
  //       <LendRequests />
  //     </Router>
  //   );

  //   await waitFor(() => {
  //     expect(screen.getByText('2024-08-01')).toBeInTheDocument();
  //   });

  //   fireEvent.click(screen.getByRole('button', { name: /accept/i }));

  //   expect(screen.getByText('Confirm Action')).toBeInTheDocument();
  //   expect(screen.getByText('Are you sure you want to accept it?')).toBeInTheDocument();

  //   fireEvent.click(screen.getByRole('button', { name: /no/i }));

  //   expect(screen.queryByText('Confirm Action')).not.toBeInTheDocument();
  // });

  // test('submits accept request on confirmation', async () => {
  //   axios.post.mockResolvedValueOnce({
  //     data: [
  //       {
  //         lendRequestId: 1,
  //         createdAt: '2024-08-01',
  //         amount: 1000,
  //         interestRate: 5,
  //         repaymentPeriod: 12,
  //         total: 1050,
  //         requestedByFirstName: 'John',
  //         requestedByLastName: 'Doe',
  //         requestedUserId: 1,
  //       },
  //     ],
  //   }).mockResolvedValueOnce({}); // Mock accept API response

  //   render(
  //     <Router>
  //       <LendRequests />
  //     </Router>
  //   );

  //   await waitFor(() => {
  //     expect(screen.getByText('2024-08-01')).toBeInTheDocument();
  //   });

  //   fireEvent.click(screen.getByRole('button', { name: /accept/i }));

  //   expect(screen.getByText('Confirm Action')).toBeInTheDocument();

  //   fireEvent.click(screen.getByRole('button', { name: /yes/i }));

  //   await waitFor(() => {
  //     expect(axios.post).toHaveBeenCalledWith(
  //       `${backendUrl}/users/lendrequests/accept`,
  //       expect.objectContaining({
  //         lendRequestId: 1,
  //         acceptorId: expect.any(Number),
  //       })
  //     );
  //   });

  //   expect(screen.queryByText('Confirm Action')).not.toBeInTheDocument();
  // });
});
