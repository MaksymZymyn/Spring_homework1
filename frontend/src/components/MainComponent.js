import React, { useState, useEffect } from 'react';
import WithdrawForm from './WithdrawForm';
import DepositForm from './DepositForm';
import TransferForm from './TransferForm';
import CustomerForm from './CustomerForm';
import AccountForm from './AccountForm';

export default function MainComponent() {
    const [customer, setCustomer] = useState(null);
    const [accounts, setAccounts] = useState([]);

    const handleAction = () => {
        // Оновлення даних для CustomerForm і AccountForm
        fetchCustomerData();
        fetchAccountsData();
    };

    const fetchCustomerData = async () => {
        // Отримання даних клієнта з сервера
        const response = await fetch('http://localhost:9000/customers/1'); // Замість '1' використовуйте реальний ID клієнта
        const data = await response.json();
        setCustomer(data);
    };

    const fetchAccountsData = async () => {
        // Отримання даних акаунтів з сервера
        const response = await fetch(`http://localhost:9000/customers/1/accounts`); // Замість '1' використовуйте реальний ID клієнта
        const data = await response.json();
        setAccounts(data);
    };

    useEffect(() => {
        fetchCustomerData();
        fetchAccountsData();
    }, []);

    const handleSaveCustomer = (updatedCustomer) => {
        // Логіка для збереження оновленого клієнта
        setCustomer(updatedCustomer);
    };

    const handleSaveAccount = async (customerId, currency) => {
        try {
            const response = await fetch(`http://localhost:9000/customers/${customerId}/accounts`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ currency }),
            });

            if (response.ok) {
                console.log('Account created');
                fetchAccountsData();
            } else {
                alert('Failed to create account. Please try again.');
            }
        } catch (error) {
            console.error('Error creating account:', error);
        }
    };

    return (
        <div>
            <WithdrawForm onWithdraw={handleAction} />
            <DepositForm onDeposit={handleAction} />
            <TransferForm onTransfer={handleAction} />
            <CustomerForm
                customer={customer}
                onSave={handleSaveCustomer}
                onCancel={() => setCustomer(null)}
            />
            {customer && (
                <AccountForm
                    customer={customer}
                    onSave={handleSaveAccount}
                    onCancel={() => setCustomer(null)}
                />
            )}
        </div>
    );
}
