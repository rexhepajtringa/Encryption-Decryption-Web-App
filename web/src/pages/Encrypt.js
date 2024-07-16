import React from 'react';
import { Routes, Route, useLocation } from 'react-router-dom';
import { Box, Heading } from '@chakra-ui/react';
import Navbar from '../components/Navbar';
import AES from '../components/AES';
import RSA from '../components/RSA';
import DSA from '../components/DSA';

const Encrypt = () => {
  const location = useLocation();

  return (
    <Box>
      <Navbar />
      <Box p={5}>
        {(location.pathname === '/encrypt' || location.pathname === '/encrypt/')  && (
          <Heading as="h1" mb={4}>Select the algorithm you want to use:</Heading>
        )}
        <Routes>
          <Route path="aes" element={<AES />} />
          <Route path="rsa" element={<RSA />} />
          <Route path="dsa" element={<DSA />} />
        </Routes>
      </Box>
    </Box>
  );
};

export default Encrypt;
