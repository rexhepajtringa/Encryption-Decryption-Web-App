import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Box, Flex, Button } from '@chakra-ui/react';

const Navbar = () => {
  const location = useLocation();

  return (
    <Box bg="teal.500" px={4}>
      <Flex h={16} alignItems="center" justifyContent="center">
        <Button 
          as={Link} 
          to="/encrypt/aes" 
          colorScheme="whiteAlpha" 
          variant="outline" 
          mx={4} 
          size="lg"
          _hover={{ bg: "white", color: "teal.500" }}
          isActive={location.pathname === '/encrypt/aes' || location.pathname === '/encrypt/aes/'}
          _active={{ bg: "white", color: "teal.500" }}
        >
          AES
        </Button>
        <Button 
          as={Link} 
          to="/encrypt/rsa" 
          colorScheme="whiteAlpha" 
          variant="outline" 
          mx={4} 
          size="lg"
          _hover={{ bg: "white", color: "teal.500" }}
          isActive={location.pathname === '/encrypt/rsa' || location.pathname === '/encrypt/rsa/'}
          _active={{ bg: "white", color: "teal.500" }}
        >
          RSA
        </Button>
        <Button 
          as={Link} 
          to="/encrypt/dsa" 
          colorScheme="whiteAlpha" 
          variant="outline" 
          mx={4} 
          size="lg"
          _hover={{ bg: "white", color: "teal.500" }}
          isActive={location.pathname === '/encrypt/dsa' || location.pathname === '/encrypt/dsa/'}
          _active={{ bg: "white", color: "teal.500" }}
        >
          DSA
        </Button>
      </Flex>
    </Box>
  );
};

export default Navbar;
