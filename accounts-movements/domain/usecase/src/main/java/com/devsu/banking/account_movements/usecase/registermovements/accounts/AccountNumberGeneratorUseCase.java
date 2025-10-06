package com.devsu.banking.account_movements.usecase.registermovements.accounts;

import java.security.SecureRandom;

class AccountNumberGeneratorUseCase {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateAccountNumber() {
        byte[] bytes = new byte[6]; // 6 bytes = 12 hex chars
        RANDOM.nextBytes(bytes);
        StringBuilder sb = new StringBuilder(12);
        for (byte b : bytes) {
            sb.append(String.format("%02X", b)); // mayúsculas, hex de 2 dígitos
        }
        return sb.toString();
    }

}

