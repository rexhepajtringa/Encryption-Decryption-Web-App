import React, { useState } from 'react';
import { Box, Heading, Button, Input, FormControl, FormLabel, VStack } from '@chakra-ui/react';
import { useAuth } from '../context/AuthContext';
import { createKeyStore } from '../services/api';

function Home() {
    const { setToken } = useAuth();
    const [password, setPassword] = useState('');
    const [name, setName] = useState(''); // New state variable for the name

    const handleLogin = () => {
        createKeyStore(password, name).then((token) => {
            setToken(password, name);
        });
    };

    return (
        <Box display="flex" justifyContent="center" alignItems="center" height="100vh">
            <VStack spacing={4} align="stretch" width="300px">
                <Heading as="h1" textAlign="center">Home Page</Heading>
                <FormControl>
                    <FormLabel htmlFor="name">Name</FormLabel>
                    <Input
                        id="name"
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)} // Update the name state variable when the input changes
                    />
                </FormControl>
                <FormControl>
                    <FormLabel htmlFor="password">Key Store Password</FormLabel>
                    <Input
                        id="password"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                </FormControl>
                <Button colorScheme="teal" size="lg" onClick={handleLogin}>
                    Log In
                </Button>
            </VStack>
        </Box>
    );
}

export default Home;