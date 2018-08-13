// Copyright (C) 2018 Michael Menzel
// 
// This file is part of Enhort. <https://enhort.mni.thm.de>.
// 
// Enhort is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// Enhort is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with Enhort.  If not, see <https://www.gnu.org/licenses/>.  
package de.thm.security;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class Crypt {

    public static SealedObject encrypt(Serializable object, String secret) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, IllegalBlockSizeException {

        Key key = new SecretKeySpec(secret.getBytes(), "AES");
        Cipher c = Cipher.getInstance("AES");

        c.init(Cipher.ENCRYPT_MODE, key);

        return new SealedObject(object, c);

    }


    public static Object decrypt(SealedObject container, String secret) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException {
        Key key = new SecretKeySpec(secret.getBytes(), "AES");
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE, key);

        return container.getObject(c);
    }
}
