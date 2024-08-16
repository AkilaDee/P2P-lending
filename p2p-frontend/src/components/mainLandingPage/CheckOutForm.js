import React from 'react';
import { CardElement } from '@stripe/react-stripe-js';
import Button from "../../components/Dashboard/Button/Button.js";

const CheckoutForm = ({ onClose }) => {
  const handleSubmit = (event) => {
    event.preventDefault();
    //
    onClose();
    alert('Payment form submitted (simulated)');
  };

  return (
    <form onSubmit={handleSubmit}>
      <CardElement />
      <Button type="submit" color="primary">
        Pay
      </Button>
    </form>
  );
};

export default CheckoutForm;