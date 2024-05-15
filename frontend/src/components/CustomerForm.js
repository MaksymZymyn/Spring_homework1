import React, { useState } from 'react';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Box from '@mui/material/Box';

export default function CustomerForm({ onSave, onCancel, customer }) {
    const [name, setName] = useState(customer ? customer.name : '');
    const [email, setEmail] = useState(customer ? customer.email : '');
    const [age, setAge] = useState(customer ? customer.age : '');

    const createCustomer = async (newCustomer) => {
        try {
            const response = await fetch('http://localhost:9000/customers', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(newCustomer),
            });
            if(response.ok) {
                alert("Customer created");
            } else {
                alert('Something went wrong. Not created');
            }
        } catch (error) {
            console.error('Something went wrong', error);
        }
    };

    const handleSave = () => {
        if(age < 18) {
            alert("Enter correct age. More than 18");
            return;
        }
        const updatedCustomer = {
            id: customer ? customer.id : null,
            name,
            email,
            age,
        };
        if(customer === null) {
            createCustomer(updatedCustomer);
            setName("");
            setEmail("");
            setAge("");
        } else {
            onSave(updatedCustomer);
        }
    };

    return (
        customer ? (
            <div>
                <h2>Edit Customer</h2>
                <form>
                    <label>
                        Name:
                        <input
                            type="text"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                        />
                    </label>
                    <br />
                    <label>
                        Email:
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                        />
                    </label>
                    <br />
                    <label>
                        Age:
                        <input
                            type="number"
                            value={age}
                            min="18"
                            onChange={(e) => setAge(e.target.value)}
                        />
                    </label>
                    <br />
                    <button type="button" onClick={handleSave}>
                        Save
                    </button>
                    <button type="button" onClick={onCancel}>
                        Cancel
                    </button>
                </form>
            </div>
        ) : (
            <Box sx={{ mt: 4 }}>
                <TextField
                    label="Name"
                    variant="outlined"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    fullWidth
                    margin="normal"
                />
                <TextField
                    label="Email"
                    variant="outlined"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    fullWidth
                    margin="normal"
                />
                <TextField
                    label="Age"
                    variant="outlined"
                    type="number"
                    value={age}
                    min="18"
                    onChange={(e) => setAge(e.target.value)}
                    fullWidth
                    margin="normal"
                />
                <Button variant="contained" onClick={handleSave}>
                    New customer
                </Button>
            </Box>
        )
    );
}
