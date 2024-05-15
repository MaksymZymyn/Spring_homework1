import React, { useState } from 'react';

export default function AccountForm({ onSave, onCancel, customer }) {
  const [currency, setCurrency] = useState('');

  const handleSaveAccount = () => {
    onSave(customer.id, currency);
    setCurrency('');
  };

  return (
      <div>
        <h2>Add Account</h2>
        <form>
          <label>
            Currency:
            <select value={currency} onChange={(e) => setCurrency(e.target.value)}>
              <option value="">Select Currency</option>
              <option value="USD">USD</option>
              <option value="EUR">EUR</option>
              <option value="UAH">UAH</option>
              <option value="CHF">CHF</option>
              <option value="GBP">GBP</option>
            </select>
          </label>
          <br />
          <button type="button" onClick={handleSaveAccount}>
            Save Account
          </button>
          <button type="button" onClick={onCancel}>
            Cancel
          </button>
        </form>
      </div>
  );
}
