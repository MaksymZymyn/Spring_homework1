import React, { useEffect, useState } from 'react';
import Link from '@mui/material/Link';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Title from './Title';
import AccountForm from './AccountForm';
import CustomerForm from './CustomerForm';

export default function Customers() {
  const [customers, setCustomers] = useState([]);
  const [editingCustomerId, setEditingCustomerId] = useState(null);
  const [addingAccountCustomerId, setAddingAccountCustomerId] = useState(null);

  useEffect(() => {
    fetchCustomers();
  }, []);

  const fetchCustomers = async () => {
    try {
      const response = await fetch('http://localhost:9000/customers');
      const data = await response.json();
      setCustomers(data);
    } catch (error) {
      console.error('Error fetching customers:', error);
    }
  };

  const updateCustomer = async (customerId, updatedCustomer) => {
    try {
      const response = await fetch(`http://localhost:9000/customers/${customerId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(updatedCustomer),
      });

      if (response.ok) {
        fetchCustomers();
        setEditingCustomerId(null);
        alert('Customer updated successfully.');
      } else {
        alert('Failed to update customer. Please try again.');
      }
    } catch (error) {
      console.error('Error updating customer:', error);
      alert('Error updating customer.');
    }
  };

  const deleteCustomer = async (customerId) => {
    try {
      const response = await fetch(`http://localhost:9000/customers/${customerId}`, {
        method: 'DELETE',
      });

      if (response.ok) {
        fetchCustomers();
        alert('Customer deleted successfully.');
      } else {
        alert('Failed to delete customer. Please try again.');
      }
    } catch (error) {
      console.error('Error deleting customer:', error);
      alert('Error deleting customer.');
    }
  };

  const addAccountToCustomer = async (customerId, currency) => {
    try {
      const response = await fetch(`http://localhost:9000/customers/${customerId}/accounts`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ currency }),
      });

      if (response.ok) {
        fetchCustomers();
        setAddingAccountCustomerId(null);
        alert('Account added successfully.');
      } else {
        alert('Failed to add account. Please try again.');
      }
    } catch (error) {
      console.error('Error adding account to customer:', error);
      alert('Error adding account.');
    }
  };

  const depositToAccount = async (customerId, accountNumber, amount) => {
    if (amount <= 0) {
      alert("Amount can't be less than or equal to 0");
      return;
    }

    try {
      const response = await fetch(`http://localhost:9000/accounts/deposit/${accountNumber}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ amount }),
      });

      if (response.ok) {
        fetchCustomers();
        alert('Deposit successful.');
      } else {
        alert('Deposit failed. Please try again.');
      }
    } catch (error) {
      console.error('Error depositing to account:', error);
      alert('Error depositing to account.');
    }
  };

  const withdrawFromAccount = async (customerId, accountNumber, amount) => {
    if (amount <= 0) {
      alert("Amount can't be less than or equal to 0");
      return;
    }

    try {
      const response = await fetch(`http://localhost:9000/accounts/withdraw/${accountNumber}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ amount }),
      });

      if (response.ok) {
        fetchCustomers();
        alert('Withdrawal successful.');
      } else {
        alert('Withdrawal failed. Not enough money. Please try again.');
      }
    } catch (error) {
      console.error('Error withdrawing from account:', error);
      alert('Error withdrawing from account.');
    }
  };

  const handleEditButtonClick = (customerId) => {
    setEditingCustomerId(customerId);
  };

  const handleCancelEdit = () => {
    setEditingCustomerId(null);
  };

  const handleSaveCustomer = (customer) => {
    if (editingCustomerId) {
      updateCustomer(editingCustomerId, customer);
    }
  };

  const handleSaveNewAccount = (customerId, currency) => {
    if (currency) {
      addAccountToCustomer(addingAccountCustomerId, currency);
    }
  };

  const handleDeposit = (customerId, accountNumber) => {
    const amount = parseFloat(prompt('Enter amount to deposit'));
    if (!isNaN(amount)) {
      depositToAccount(customerId, accountNumber, amount);
    }
  };

  const handleWithdraw = (customerId, accountNumber) => {
    const amount = parseFloat(prompt('Enter amount to withdraw'));
    if (!isNaN(amount)) {
      withdrawFromAccount(customerId, accountNumber, amount);
    }
  };

  return (
      <React.Fragment>
        {customers.map((customer) => (
            <React.Fragment key={customer.id}>
              {editingCustomerId === customer.id ? (
                  <React.Fragment>
                    <Title>Editing {customer.name}</Title>
                    <CustomerForm
                        onSave={handleSaveCustomer}
                        onCancel={handleCancelEdit}
                        customer={customer}
                    />
                  </React.Fragment>
              ) : (
                  <React.Fragment>
                    <Title>
                      {customer.name} (email: {customer.email}, age: {customer.age})
                    </Title>
                    <Table size="small">
                      <TableHead>
                        <TableRow>
                          <TableCell>Account Number</TableCell>
                          <TableCell>Currency</TableCell>
                          <TableCell>Balance</TableCell>
                          <TableCell>Actions</TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {customer.accounts.map((account) => (
                            <TableRow key={account.id}>
                              <TableCell>{account.number}</TableCell>
                              <TableCell>{account.currency}</TableCell>
                              <TableCell>{account.balance}</TableCell>
                              <TableCell>
                                <button onClick={() => handleDeposit(customer.id, account.number)}>Deposit</button>
                                <button onClick={() => handleWithdraw(customer.id, account.number)}>Withdraw</button>
                              </TableCell>
                            </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                    {addingAccountCustomerId === customer.id ? (
                        <AccountForm
                            onSave={(currency) => handleSaveNewAccount(customer.id, currency)}
                            onCancel={() => setAddingAccountCustomerId(null)}
                            customer={customer}
                            id={customer.id}
                        />
                    ) : (
                        <Link
                            color="primary"
                            href="#"
                            onClick={() => setAddingAccountCustomerId(customer.id)}
                            sx={{ mt: 3 }}
                        >
                          Add Account
                        </Link>
                    )}
                    <Link
                        color="primary"
                        href="#"
                        onClick={() => handleEditButtonClick(customer.id)}
                        sx={{ mt: 3 }}
                    >
                      Edit Customer
                    </Link>
                    <button onClick={() => deleteCustomer(customer.id)}>Delete Customer</button>
                  </React.Fragment>
              )}
            </React.Fragment>
        ))}
      </React.Fragment>
  );
}
