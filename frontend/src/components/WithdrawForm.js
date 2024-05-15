import React, { useState } from 'react';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Box from '@mui/material/Box';

export default function WithdrawForm({ onWithdraw }) {
    const [accountNumber, setAccountNumber] = useState('');
    const [amount, setAmount] = useState('');

    const handleWithdraw = async () => {
        try {
            if (!accountNumber || !amount) {
                alert('Please enter all required fields.');
                return;
            }

            if (amount <= 0) {
                alert("Amount can't be less than 0");
                return;
            }

            const response = await fetch(`http://localhost:9000/accounts/withdraw/${accountNumber}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(amount),
            });

            if (response.ok) {
                alert('Withdrawal successful');
                onWithdraw();
                setAccountNumber('');
                setAmount('');
            } else {
                alert('Not enough money. Please try again.');
            }
        } catch (error) {
            console.error('Error withdrawing:', error);
        }
    };

    return (
        <Box sx={{ mt: 4 }}>
            <TextField
                label="Account Number"
                variant="outlined"
                value={accountNumber}
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
            <Button variant="contained" onClick={handleWithdraw}>
                Withdraw
            </Button>
        </Box>
    );
}
