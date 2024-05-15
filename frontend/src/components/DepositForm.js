import React, { useState } from 'react';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Box from '@mui/material/Box';

export default function DepositForm({ onDeposit }) {
  const [aNumber, setAccountNumber] = useState('');
  const [amount, setAmount] = useState('');

  const handleDeposit = async () => {
    try {
      if (!aNumber || !amount) {
        alert('Please enter all required fields.');
        return;
      }
      if (amount <= 0) {
        alert("Amount can't be less than 0");
        return;
      }
      const response = await fetch(`http://localhost:9000/accounts/deposit/${aNumber}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(amount),
      });

      if (response.ok) {
        alert('Deposit successful');
        onDeposit();
        setAccountNumber('');
        setAmount('');
      } else {
        alert('Deposit failed. Please try again.');
      }
    } catch (error) {
      console.error('Error depositing:', error);
    }
  };

  return (
      <Box sx={{ mt: 4 }}>
        <TextField
            label="Account number"
            variant="outlined"
            value={aNumber}
            onChange={(e) => setAccountNumber(e.target.value)}
            fullWidth
            margin="normal"
        />
        <TextField
            label="Amount"
            variant="outlined"
            type="number"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            fullWidth
            margin="normal"
        />
        <Button variant="contained" onClick={handleDeposit}>
          Deposit
        </Button>
      </Box>
  );
}
