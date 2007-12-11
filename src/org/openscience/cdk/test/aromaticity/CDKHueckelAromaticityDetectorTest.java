/* $Revision: 8225 $
 * $Author: egonw $
 * $Date: 2007-04-21 02:23:13 +0200 (Sat, 21 Apr 2007) $
 *
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@list.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.test.aromaticity;

import java.io.InputStream;
import java.util.Iterator;

import javax.vecmath.Point2d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;

/**
 * @author steinbeck
 * @author egonw
 * @cdk.module test-standard
 * @cdk.created 2002-10-06
 */
public class CDKHueckelAromaticityDetectorTest extends CDKTestCase {

    /**
     * Constructor for the HueckelAromaticityDetectorTest object
     *
     * @param name Description of the Parameter
     */
    public CDKHueckelAromaticityDetectorTest(String name) {
        super(name);
    }


    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(CDKHueckelAromaticityDetectorTest.class);
    }

    public void testDetectAromaticity_IAtomContainer() throws Exception {
        IMolecule mol = makeAromaticMolecule();

        AtomContainerManipulator.percieveAtomTypesAndConfigerAtoms(mol);
        boolean isAromatic = CDKHueckelAromaticityDetector.detectAromaticity(mol);
        assertTrue("Molecule is expected to be marked aromatic!", isAromatic);

        int numberOfAromaticAtoms = 0;
        for (int i = 0; i < mol.getAtomCount(); i++) {
            if (((IAtom) mol.getAtom(i)).getFlag(CDKConstants.ISAROMATIC))
                numberOfAromaticAtoms++;
        }
        assertEquals(6, numberOfAromaticAtoms);

        int numberOfAromaticBonds = 0;
        for (int i = 0; i < mol.getBondCount(); i++) {
            if (((IBond) mol.getBond(i)).getFlag(CDKConstants.ISAROMATIC))
                numberOfAromaticBonds++;
        }
        assertEquals(6, numberOfAromaticBonds);

    }

    public void testCDKHueckelAromaticityDetector() {
        // For autogenerated constructor
        CDKHueckelAromaticityDetector detector = new CDKHueckelAromaticityDetector();
        assertNotNull(detector);
    }

    public void testNMethylPyrrol() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IMolecule mol = sp.parseSmiles("c1ccn(C)c1");
        CDKHueckelAromaticityDetector.detectAromaticity(mol);

        IRingSet ringset = (new SSSRFinder(mol)).findSSSR();
        int numberOfAromaticRings = 0;
        RingSetManipulator.markAromaticRings(ringset);
        for (int i = 0; i < ringset.getAtomContainerCount(); i++) {
            if (ringset.getAtomContainer(i).getFlag(CDKConstants.ISAROMATIC))
                numberOfAromaticRings++;
        }
        assertEquals(1, numberOfAromaticRings);
    }

    public void testPyridine() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IMolecule mol = sp.parseSmiles("c1ccncc1");
        CDKHueckelAromaticityDetector.detectAromaticity(mol);

        IRingSet ringset = (new SSSRFinder(mol)).findSSSR();
        int numberOfAromaticRings = 0;
        RingSetManipulator.markAromaticRings(ringset);
        for (int i = 0; i < ringset.getAtomContainerCount(); i++) {
            if (ringset.getAtomContainer(i).getFlag(CDKConstants.ISAROMATIC))
                numberOfAromaticRings++;
        }
        assertEquals(1, numberOfAromaticRings);
    }
    
    public void testPyridineOxide() throws Exception {
		Molecule molecule = MoleculeFactory.makePyridineOxide();
		AtomContainerManipulator.percieveAtomTypesAndConfigerAtoms(molecule);
		assertTrue(CDKHueckelAromaticityDetector.detectAromaticity(molecule));
	}
    
    public void testPyridineOxide_SP2() throws Exception {
		Molecule molecule = MoleculeFactory.makePyridineOxide();
		Iterator<IBond> bonds = molecule.bonds();
		while (bonds.hasNext()) bonds.next().setOrder(CDKConstants.BONDORDER_SINGLE);
		for (int i=0; i<6; i++) {
			molecule.getAtom(i).setHybridization(IAtomType.Hybridization.SP2);
		}
		AtomContainerManipulator.percieveAtomTypesAndConfigerAtoms(molecule);
		assertTrue(CDKHueckelAromaticityDetector.detectAromaticity(molecule));
	}

    public void testFuran() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IMolecule mol = sp.parseSmiles("c1cocc1");
        assertTrue("Molecule is not detected aromatic", CDKHueckelAromaticityDetector.detectAromaticity(mol));

        IRingSet ringset = (new SSSRFinder(mol)).findSSSR();
        int numberOfAromaticRings = 0;
        RingSetManipulator.markAromaticRings(ringset);
        for (int i = 0; i < ringset.getAtomContainerCount(); i++) {
            if (ringset.getAtomContainer(i).getFlag(CDKConstants.ISAROMATIC))
                numberOfAromaticRings++;
        }
        assertEquals(1, numberOfAromaticRings);
    }

    /**
     * A unit test for JUnit The special difficulty with Azulene is that only the
     * outermost larger 10-ring is aromatic according to Hueckel rule.
     */
    public void testAzulene() throws Exception {
        boolean[] testResults =
                {true,
                        true,
                        true,
                        true,
                        true,
                        true,
                        true,
                        true,
                        true,
                        true
                };
        Molecule molecule = MoleculeFactory.makeAzulene();
        AtomContainerManipulator.percieveAtomTypesAndConfigerAtoms(molecule);
        CDKHueckelAromaticityDetector.detectAromaticity(molecule);
        for (int f = 0; f < molecule.getAtomCount(); f++) {
            assertEquals("Atom " + f + " is not correctly marked",
                    testResults[f], molecule.getAtom(f).getFlag(CDKConstants.ISAROMATIC));
        }
    }


    /**
     * A unit test for JUnit. The N has to be counted correctly.
     */
    public void testIndole() throws Exception {
        Molecule molecule = MoleculeFactory.makeIndole();
        boolean testResults[] = {
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true
        };
        //boolean isAromatic = false;
        AtomContainerManipulator.percieveAtomTypesAndConfigerAtoms(molecule);
        CDKHueckelAromaticityDetector.detectAromaticity(molecule);
        for (int f = 0; f < molecule.getAtomCount(); f++) {
            assertEquals(
                    "Atom " + f + " is not correctly marked",
                    testResults[f],
                    molecule.getAtom(f).getFlag(CDKConstants.ISAROMATIC)
            );
        }
    }

    /**
     * A unit test for JUnit. The N has to be counted correctly.
     */
    public void testPyrrole() throws Exception {
        Molecule molecule = MoleculeFactory.makePyrrole();
        boolean testResults[] = {
                true,
                true,
                true,
                true,
                true
        };
        AtomContainerManipulator.percieveAtomTypesAndConfigerAtoms(molecule);
        CDKHueckelAromaticityDetector.detectAromaticity(molecule);
        for (int f = 0; f < molecule.getAtomCount(); f++) {
            assertEquals(
                    "Atom " + f + " is not correctly marked",
                    testResults[f],
                    molecule.getAtom(f).getFlag(CDKConstants.ISAROMATIC)
            );
        }
    }


    /**
     * A unit test for JUnit
     */
    public void testThiazole() throws Exception {
        Molecule molecule = MoleculeFactory.makeThiazole();
        AtomContainerManipulator.percieveAtomTypesAndConfigerAtoms(molecule);
        assertTrue("Molecule is not detected as aromatic", CDKHueckelAromaticityDetector.detectAromaticity(molecule));

        for (int f = 0; f < molecule.getAtomCount(); f++) {
            assertTrue(
                "Atom " + f + " is not correctly marked",
                molecule.getAtom(f).getFlag(CDKConstants.ISAROMATIC)
            );
        }
    }


    /**
     * A unit test for JUnit
     */
    public void testTetraDehydroDecaline() throws Exception {
        boolean isAromatic = false;
        //boolean testResults[] = {true, false, false};
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IMolecule mol = sp.parseSmiles("C1CCCc2c1cccc2");
        AtomContainerManipulator.percieveAtomTypesAndConfigerAtoms(mol);
        CDKHueckelAromaticityDetector.detectAromaticity(mol);
        IRingSet rs = (new AllRingsFinder()).findAllRings(mol);
        RingSetManipulator.markAromaticRings(rs);
        IRing r = null;
        int i = 0, aromacount = 0;
        Iterator<IAtomContainer> rings = rs.atomContainers();
        while (rings.hasNext()) {
            r = (IRing) rings.next();
            isAromatic = r.getFlag(CDKConstants.ISAROMATIC);

            if (isAromatic) aromacount++;
            i++;
        }
        assertEquals(1, aromacount);
    }

    /**
     * This is a bug reported for JCP.
     *
     * @cdk.bug 956924
     */
    public void testSFBug956924() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IMolecule mol = sp.parseSmiles("[cH+]1cccccc1"); // tropylium cation
        assertEquals(IAtomType.Hybridization.PLANAR3, mol.getAtom(0).getHybridization());
        for (int f = 1; f < mol.getAtomCount(); f++) {
            assertEquals(IAtomType.Hybridization.SP2, mol.getAtom(f).getHybridization());
        }
        assertTrue(CDKHueckelAromaticityDetector.detectAromaticity(mol));
        assertEquals(7, mol.getAtomCount());
        for (int f = 0; f < mol.getAtomCount(); f++) {
            assertNotNull(mol.getAtom(f));
            assertTrue(mol.getAtom(f).getFlag(CDKConstants.ISAROMATIC));
        }
    }

    /**
     * This is a bug reported for JCP.
     *
     * @cdk.bug 956923
     */
    public void testSFBug956923() throws Exception {
        boolean testResults[] = {false, false, false, false, false, false, false, false};
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IMolecule mol = sp.parseSmiles("O=c1cccccc1"); // tropone
        AtomContainerManipulator.percieveAtomTypesAndConfigerAtoms(mol);
        assertFalse(CDKHueckelAromaticityDetector.detectAromaticity(mol));
        assertEquals(testResults.length, mol.getAtomCount());
        for (int f = 0; f < mol.getAtomCount(); f++) {
            assertEquals(testResults[f], mol.getAtom(f).getFlag(CDKConstants.ISAROMATIC));
        }
    }

    /**
     * A unit test for JUnit
     */
    public void testPorphyrine() throws Exception {
        boolean isAromatic = false;
        boolean testResults[] = {
                false,
                false,
                false,
                false,
                false,
                true,
                true,
                true,
                true,
                true,
                false,
                true,
                true,
                true,
                false,
                true,
                true,
                false,
                false,
                true,
                true,
                false,
                false,
                false,
                true,
                true,
                false,
                false,
                false,
                true,
                true,
                false,
                false,
                false,
                false,
                true,
                true,
                true,
                true,
                false,
                false,
                false
        };

        String filename = "data/mdl/porphyrin.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IMolecule molecule = (IMolecule) reader.read(DefaultChemObjectBuilder.getInstance().newMolecule());

        AtomContainerManipulator.percieveAtomTypesAndConfigerAtoms(molecule);
        isAromatic = CDKHueckelAromaticityDetector.detectAromaticity(molecule);
        for (int f = 0; f < molecule.getAtomCount(); f++) {
            assertEquals(testResults[f], molecule.getAtom(f).getFlag(CDKConstants.ISAROMATIC));
        }
        assertTrue(isAromatic);
    }


    /**
     * A unit test for JUnit
     *
     * @cdk.bug 698152
     */
    public void testBug698152() throws Exception {
        //boolean isAromatic = false;
        boolean[] testResults = {true,
                true,
                true,
                true,
                true,
                true,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false};

        String filename = "data/mdl/bug698152.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IMolecule molecule = (IMolecule) reader.read(DefaultChemObjectBuilder.getInstance().newMolecule());

        AtomContainerManipulator.percieveAtomTypesAndConfigerAtoms(molecule);
        CDKHueckelAromaticityDetector.detectAromaticity(molecule);
        for (int f = 0; f < molecule.getAtomCount(); f++) {
            assertEquals(testResults[f], molecule.getAtom(f).getFlag(CDKConstants.ISAROMATIC));
        }
    }

    /**
     * A test for the fix of bug #716259, where a quinone ring
     * was falsely detected as aromatic.
     *
     * @cdk.bug 716259
     */
    public void testBug716259() throws Exception {
        IMolecule molecule = null;
        //boolean isAromatic = false;
        boolean[] testResults = {
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false
        };

        String filename = "data/mdl/bug716259.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        molecule = (IMolecule) reader.read(DefaultChemObjectBuilder.getInstance().newMolecule());

        AtomContainerManipulator.percieveAtomTypesAndConfigerAtoms(molecule);
        CDKHueckelAromaticityDetector.detectAromaticity(molecule);
        for (int f = 0; f < molecule.getAtomCount(); f++) {
            assertEquals(testResults[f], molecule.getAtom(f).getFlag(CDKConstants.ISAROMATIC));
        }

    }


    /**
     * A unit test for JUnit
     */
    public void testQuinone() throws Exception {
        Molecule molecule = MoleculeFactory.makeQuinone();
        boolean[] testResults = {false, false, false, false, false, false, false, false};

        CDKHueckelAromaticityDetector.detectAromaticity(molecule);
        for (int f = 0; f < molecule.getAtomCount(); f++) {
            assertEquals(testResults[f], molecule.getAtom(f).getFlag(CDKConstants.ISAROMATIC));
        }

    }

    /**
     * @cdk.bug 1328739
     */
    public void testBug1328739() throws Exception {
        String filename = "data/mdl/bug1328739.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IMolecule molecule = (IMolecule) reader.read(DefaultChemObjectBuilder.getInstance().newMolecule());

        AtomContainerManipulator.percieveAtomTypesAndConfigerAtoms(molecule);
        CDKHueckelAromaticityDetector.detectAromaticity(molecule);

        assertEquals(15, molecule.getBondCount());
        assertTrue(molecule.getBond(0).getFlag(CDKConstants.ISAROMATIC));
        assertTrue(molecule.getBond(1).getFlag(CDKConstants.ISAROMATIC));
        assertTrue(molecule.getBond(2).getFlag(CDKConstants.ISAROMATIC));
        assertTrue(molecule.getBond(3).getFlag(CDKConstants.ISAROMATIC));
        assertTrue(molecule.getBond(4).getFlag(CDKConstants.ISAROMATIC));
        assertTrue(molecule.getBond(6).getFlag(CDKConstants.ISAROMATIC));
    }

    /**
     * A unit test for JUnit
     */
    public void testBenzene() throws Exception {
        Molecule molecule = MoleculeFactory.makeBenzene();
        AtomContainerManipulator.percieveAtomTypesAndConfigerAtoms(molecule);
        CDKHueckelAromaticityDetector.detectAromaticity(molecule);
        for (int f = 0; f < molecule.getAtomCount(); f++) {
            assertTrue(molecule.getAtom(f).getFlag(CDKConstants.ISAROMATIC));
        }
    }

    public void testCyclobutadiene() throws Exception {
        // anti-aromatic
        Molecule molecule = MoleculeFactory.makeCyclobutadiene();
        AtomContainerManipulator.percieveAtomTypesAndConfigerAtoms(molecule);

        assertFalse(CDKHueckelAromaticityDetector.detectAromaticity(molecule));
        for (int f = 0; f < molecule.getAtomCount(); f++) {
            assertFalse(molecule.getAtom(f).getFlag(CDKConstants.ISAROMATIC));
        }
    }

    private IMolecule makeAromaticMolecule() {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom a1 = mol.getBuilder().newAtom("C");
        a1.setPoint2d(new Point2d(329.99999999999994, 971.0));
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newAtom("C");
        a2.setPoint2d(new Point2d(298.8230854637602, 989.0));
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newAtom("C");
        a3.setPoint2d(new Point2d(298.8230854637602, 1025.0));
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newAtom("C");
        a4.setPoint2d(new Point2d(330.0, 1043.0));
        mol.addAtom(a4);
        IAtom a5 = mol.getBuilder().newAtom("C");
        a5.setPoint2d(new Point2d(361.1769145362398, 1025.0));
        mol.addAtom(a5);
        IAtom a6 = mol.getBuilder().newAtom("C");
        a6.setPoint2d(new Point2d(361.1769145362398, 989.0));
        mol.addAtom(a6);
        IAtom a7 = mol.getBuilder().newAtom("C");
        a7.setPoint2d(new Point2d(392.3538290724796, 971.0));
        mol.addAtom(a7);
        IAtom a8 = mol.getBuilder().newAtom("C");
        a8.setPoint2d(new Point2d(423.5307436087194, 989.0));
        mol.addAtom(a8);
        IAtom a9 = mol.getBuilder().newAtom("C");
        a9.setPoint2d(new Point2d(423.5307436087194, 1025.0));
        mol.addAtom(a9);
        IAtom a10 = mol.getBuilder().newAtom("C");
        a10.setPoint2d(new Point2d(392.3538290724796, 1043.0));
        mol.addAtom(a10);
        IBond b1 = mol.getBuilder().newBond(a1, a2, IBond.Order.DOUBLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newBond(a2, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newBond(a3, a4, IBond.Order.DOUBLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newBond(a4, a5, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newBond(a5, a6, IBond.Order.DOUBLE);
        mol.addBond(b5);
        IBond b6 = mol.getBuilder().newBond(a6, a1, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b7 = mol.getBuilder().newBond(a6, a7, IBond.Order.SINGLE);
        mol.addBond(b7);
        IBond b8 = mol.getBuilder().newBond(a7, a8, IBond.Order.SINGLE);
        mol.addBond(b8);
        IBond b9 = mol.getBuilder().newBond(a8, a9, IBond.Order.SINGLE);
        mol.addBond(b9);
        IBond b10 = mol.getBuilder().newBond(a9, a10, IBond.Order.SINGLE);
        mol.addBond(b10);
        IBond b11 = mol.getBuilder().newBond(a10, a5, IBond.Order.SINGLE);
		  mol.addBond(b11);
		  return mol;
	}
	
}

