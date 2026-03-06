# GCP Key Manager Providers

This module provides integration between Liferay DXP and Google Cloud Platform (GCP) for secret management and identity impersonation. It follows the "Identity-First" bootstrap architecture.

---

## 1. Google ADC Identity Provider (`gcp-adc`)

**Purpose:** Establishes the ambient identity of the infrastructure where Liferay is running. It requires no static secrets.

### How it works

It leverages the Google Cloud SDK's `Application Default Credentials` (ADC). If running on GKE, GCE, or Cloud Run, it fetches short-lived tokens from the instance metadata server.

### Configuration
*   **Provider ID**: `gcp-adc` (default)
*   **Default Scopes**: OAuth2 scopes to request (e.g., `https://www.googleapis.com/auth/cloud-platform`).
*   **Initialization Phase**: 1 (Initial identity anchor).

### Usage

Reference in other configurations: `${keyref:gcp-adc/access-token}`

---

## 2. Google Secret Manager Provider (`gcp-sm`)

**Purpose:** Primary storage for encrypted secrets (passwords, API keys).

### How it works

Inherits the identity established by `gcp-adc` (or environment) to connect to the GCP Secret Manager API. It supports both reading and writing secrets.

### Configuration
*   **Provider ID**: `gcp-sm` (default)
*   **GCP Project ID**: The ID of the GCP project where secrets are stored.
*   **Initialization Phase**: 2 (Requires identity).

### Usage
*   **Latest Version**: `${keyref:gcp-sm/my-secret}`
*   **Specific Version**: `${keyref:gcp-sm/my-secret:2}`

---

## 3. Google Service Account Token Provider (`gcp-sa-token`)

**Purpose:** Generates GCP access tokens using a static Service Account JSON key, without requiring an ambient cloud identity.

### How it works

It parses a Service Account JSON key string and uses it to generate short-lived OAuth2 tokens.

### Configuration
*   **Provider ID**: `gcp-sa-token` (default)
*   **Service Account JSON Key**: The raw JSON string or a reference to it (e.g., `${keyref:keystore/gcp-json-key}`).
*   **Default Scopes**: The permissions the token should hold.

### Usage

Reference: `${keyref:gcp-sa-token/access-token}`

---

## 4. Google Service Account Impersonation Provider (`gcp-impersonation`)

**Purpose:** High-security cross-account access. Allows one identity to "act as" another service account.

### How it works

Uses the `IAM Credentials API` to generate a token for a target account. This is more secure than sharing JSON keys between projects.

### Configuration
*   **Provider ID**: `gcp-impersonation` (default)
*   **Target Service Account Email**: The email of the account to be assumed.
*   **Delegated Scopes**: Scopes for the assumed identity.

### Usage

Reference: `${keyref:gcp-impersonation/access-token}`

---

## Deployment Summary Table

| Provider | Type | Phase | Auth Method | Typical Use Case |
| :--- | :--- | :--- | :--- | :--- |
| `gcp-adc` | Identity | 1 | Metadata Server | Running inside GCP (GKE/GCE). |
| `gcp-sm` | Storage | 2 | ADC / SA | Storing DB passwords, API keys. |
| `gcp-sa-token` | Identity | 2 | JSON Key | Running outside GCP (On-prem). |
| `gcp-impersonation` | Identity | 2 | IAM API | Multi-project enterprise setups. |