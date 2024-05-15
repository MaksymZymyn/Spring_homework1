import React, { useState } from 'react';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Box from '@mui/material/Box';

export default function TransferForm({ onTransfer }) {
  const [fromAccount, setFromAccount] = useState('');
  const [toAccount, setToAccount] = useState('');
  const [amount, setAmount] = useState('');

  const handleTransfer = async () => {
    try {
      if (!fromAccount || !toAccount || !amount) {
        alert('Please enter all required fields.');
        return;
      }
      if (amount <= 0) {
        alert("Amount can't be less than 0");
        return;
      }

      const response = await fetch(`http://localhost:9000/accounts/transfer/${fromAccount}/${toAccount}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ amount }),
      });

      if (response.ok) {
        alert('Transfer successful');
        onTransfer();
        setFromAccount('');
        setToAccount('');
        setAmount('');
      } else {
        alert('Transfer failed. Not enough money. Please try again.');
      }
    } catch (error) {
      console.error('Error transferring:', error);
    }
  };

  return (
      <Box sx={{ mt: 4 }}>
        <TextField
            label="Account number transfer from"
            variant="outlined"
            value={fromAccount}
            onChange={(e) => setFromAccount(e.target.value)}
            fullWidth
            margin="normal"
        />
        <TextField
            label="Account number transfer to"
            variant="outlined"
            value={toAccount}
            onChange={(e) => setToAccount(e.target.value)}
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
        <Button variant="contained" onClick={handleTransfer}>
          Transfer
        </Button>
      </Box>
  );
}
